package com.scheduler.timezone.service;

import com.scheduler.timezone.model.Task;
import com.scheduler.timezone.model.TaskExecution;
import com.scheduler.timezone.model.TaskStatus;
import com.scheduler.timezone.repository.TaskExecutionRepository;
import com.scheduler.timezone.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {
    
    private final TaskRepository taskRepository;
    private final TaskExecutionRepository executionRepository;
    private final TimeZoneService timeZoneService;
    
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    @Transactional
    public void executeDueTasks() {
        Instant now = Instant.now();
        
        List<Task> dueTasks = taskRepository
                .findByNextRunUtcBeforeAndStatus(now, TaskStatus.ACTIVE);
        
        log.info("Found {} due tasks to execute", dueTasks.size());
        
        dueTasks.forEach(task -> {
            try {
                executeTask(task);
                scheduleNextRun(task);
            } catch (Exception e) {
                log.error("Error executing task {}: {}", task.getId(), e.getMessage());
                task.setStatus(TaskStatus.FAILED);
            }
            taskRepository.save(task);
        });
    }
    
    @Transactional
    public void executeTaskManually(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (task.getStatus() != TaskStatus.ACTIVE) {
            throw new RuntimeException("Task is not active");
        }
        
        executeTask(task);
        scheduleNextRun(task);
        taskRepository.save(task);
    }
    
    private void executeTask(Task task) {
        long startTime = System.currentTimeMillis();
        ZoneId zoneId = ZoneId.of(task.getTimeZone());
        Instant executionTime = Instant.now();
        ZonedDateTime localTime = executionTime.atZone(zoneId);
        
        log.info("Executing task: {} in timezone: {}", task.getName(), task.getTimeZone());
        
        // Simulate task execution
        try {
            Thread.sleep(100 + (long)(Math.random() * 900)); // 100-1000ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        // Record execution
        TaskExecution execution = TaskExecution.builder()
                .taskId(task.getId())
                .executionTimeUtc(executionTime)
                .executionTimeLocal(localTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .timeZone(task.getTimeZone())
                .dstInEffect(timeZoneService.isDSTActive(zoneId, executionTime))
                .utcOffset(timeZoneService.getUTCOffset(zoneId, executionTime))
                .executionStatus("SUCCESS")
                .durationMs(duration)
                .notes("Task executed successfully")
                .build();
        
        executionRepository.save(execution);
        
        // Update task
        task.setLastExecutionUtc(executionTime);
        task.setExecutionCount(task.getExecutionCount() + 1);
    }
    
    private void scheduleNextRun(Task task) {
        ZoneId zoneId = ZoneId.of(task.getTimeZone());
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        
        ZonedDateTime nextRun = timeZoneService.calculateNextExecution(
                task.getCronExpression(),
                zoneId,
                now
        );
        
        task.setNextRunUtc(nextRun.toInstant());
        
        log.info("Scheduled next run for task {} at {} ({})", 
                task.getName(), 
                nextRun.toInstant(), 
                nextRun);
    }
}
