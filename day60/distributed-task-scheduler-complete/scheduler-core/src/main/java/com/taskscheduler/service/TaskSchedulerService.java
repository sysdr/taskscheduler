package com.taskscheduler.service;

import com.taskscheduler.domain.Task;
import com.taskscheduler.domain.TaskExecution;
import com.taskscheduler.repository.TaskRepository;
import com.taskscheduler.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {
    private final TaskRepository taskRepository;
    private final TaskExecutionRepository executionRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MetricsService metricsService;
    
    private static final String LEADER_KEY = "scheduler:leader";
    private static final String LOCK_PREFIX = "task:lock:";
    
    @Scheduled(fixedDelay = 5000)
    public void scheduleTasksIfLeader() {
        if (!tryAcquireLeadership()) {
            return;
        }
        
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Task> dueTasks = taskRepository.findDueTasks(now);
            
            log.info("Found {} due tasks", dueTasks.size());
            
            for (Task task : dueTasks) {
                if (acquireTaskLock(task.getId())) {
                    dispatchTask(task);
                }
            }
            
            metricsService.recordTasksScheduled(dueTasks.size());
        } catch (Exception e) {
            log.error("Error scheduling tasks", e);
            metricsService.recordSchedulerError();
        }
    }
    
    private boolean tryAcquireLeadership() {
        try {
            String instance = InetAddress.getLocalHost().getHostName();
            Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(LEADER_KEY, instance, 10, TimeUnit.SECONDS);
            
            if (Boolean.TRUE.equals(acquired)) {
                log.debug("Acquired leadership: {}", instance);
                return true;
            }
            
            String currentLeader = redisTemplate.opsForValue().get(LEADER_KEY);
            if (instance.equals(currentLeader)) {
                redisTemplate.expire(LEADER_KEY, 10, TimeUnit.SECONDS);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error acquiring leadership", e);
            return false;
        }
    }
    
    private boolean acquireTaskLock(Long taskId) {
        try {
            String lockKey = LOCK_PREFIX + taskId;
            Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "locked", 60, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(acquired);
        } catch (Exception e) {
            log.error("Error acquiring task lock for task {}", taskId, e);
            return false;
        }
    }
    
    @Transactional
    public void dispatchTask(Task task) {
        try {
            task.setStatus(Task.TaskStatus.RUNNING);
            task.setLastExecution(LocalDateTime.now());
            task.setExecutionCount(task.getExecutionCount() + 1);
            taskRepository.save(task);
            
            TaskExecution execution = new TaskExecution();
            execution.setTaskId(task.getId());
            execution.setStartTime(LocalDateTime.now());
            execution.setStatus(TaskExecution.ExecutionStatus.STARTED);
            executionRepository.save(execution);
            
            String message = String.format("{\"taskId\":%d,\"executionId\":%d,\"handler\":\"%s\",\"params\":\"%s\"}",
                task.getId(), execution.getId(), task.getHandlerClass(), task.getTaskParameters());
            
            kafkaTemplate.send("task-executions", task.getId().toString(), message);
            
            log.info("Dispatched task {} to Kafka", task.getId());
            metricsService.recordTaskDispatched(task.getPriority());
            
        } catch (Exception e) {
            log.error("Error dispatching task {}", task.getId(), e);
            handleTaskFailure(task, e.getMessage());
        }
    }
    
    @Transactional
    public void completeTask(Long taskId, Long executionId, boolean success, String message) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        TaskExecution execution = executionRepository.findById(executionId).orElseThrow();
        
        execution.setEndTime(LocalDateTime.now());
        execution.setDurationMs(java.time.Duration.between(
            execution.getStartTime(), execution.getEndTime()).toMillis());
        
        if (success) {
            execution.setStatus(TaskExecution.ExecutionStatus.SUCCESS);
            execution.setOutput(message);
            
            task.setStatus(Task.TaskStatus.SCHEDULED);
            task.setFailureCount(0);
            calculateNextExecution(task);
            
            metricsService.recordTaskSuccess(execution.getDurationMs());
        } else {
            execution.setStatus(TaskExecution.ExecutionStatus.FAILED);
            execution.setErrorMessage(message);
            handleTaskFailure(task, message);
        }
        
        executionRepository.save(execution);
        taskRepository.save(task);
        
        releaseTaskLock(taskId);
    }
    
    private void handleTaskFailure(Task task, String error) {
        task.setFailureCount(task.getFailureCount() + 1);
        task.setLastError(error);
        
        if (task.getFailureCount() >= task.getMaxRetries()) {
            task.setStatus(Task.TaskStatus.FAILED);
            log.error("Task {} failed after {} retries", task.getId(), task.getMaxRetries());
            metricsService.recordTaskFailed();
        } else {
            task.setStatus(Task.TaskStatus.SCHEDULED);
            calculateNextExecution(task);
            log.warn("Task {} failed, retry {}/{}", task.getId(), 
                task.getFailureCount(), task.getMaxRetries());
        }
    }
    
    private void calculateNextExecution(Task task) {
        try {
            CronExpression cron = CronExpression.parse(task.getCronExpression());
            LocalDateTime next = cron.next(LocalDateTime.now(ZoneId.of(task.getTimezone())));
            task.setNextExecution(next);
        } catch (Exception e) {
            log.error("Error calculating next execution for task {}", task.getId(), e);
        }
    }
    
    private void releaseTaskLock(Long taskId) {
        String lockKey = LOCK_PREFIX + taskId;
        redisTemplate.delete(lockKey);
    }
    
    public Task createTask(Task task) {
        calculateNextExecution(task);
        return taskRepository.save(task);
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public List<TaskExecution> getTaskExecutions(Long taskId) {
        return executionRepository.findByTaskIdOrderByStartTimeDesc(taskId);
    }
}
