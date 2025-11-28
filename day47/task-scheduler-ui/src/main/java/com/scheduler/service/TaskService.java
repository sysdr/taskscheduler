package com.scheduler.service;

import com.scheduler.model.*;
import com.scheduler.repository.TaskRepository;
import com.scheduler.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final TaskExecutionRepository executionRepository;
    private final Random random = new Random();
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    @Transactional
    public Task createTask(Task task) {
        log.info("Creating new task: {}", task.getName());
        
        // Set next execution time
        if (task.getType() == TaskType.CRON || task.getType() == TaskType.FIXED_DELAY) {
            task.setNextExecution(LocalDateTime.now().plusMinutes(1));
        }
        
        return taskRepository.save(task);
    }
    
    @Transactional
    public Task updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found: " + id));
        
        task.setStatus(status);
        return taskRepository.save(task);
    }
    
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    
    public List<TaskExecution> getTaskExecutions(Long taskId) {
        return executionRepository.findByTaskIdOrderByStartedAtDesc(taskId);
    }
    
    public Optional<Task> getNextScheduledTask() {
        List<Task> scheduledTasks = taskRepository.findByStatus(TaskStatus.SCHEDULED);
        if (scheduledTasks.isEmpty()) {
            return Optional.empty();
        }
        // Return the task with the earliest next execution time
        return scheduledTasks.stream()
            .filter(task -> task.getNextExecution() != null)
            .min((t1, t2) -> t1.getNextExecution().compareTo(t2.getNextExecution()));
    }
    
    // Simulate task execution every 10 seconds
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void executeScheduledTasks() {
        List<Task> scheduledTasks = taskRepository.findByStatus(TaskStatus.SCHEDULED);
        
        for (Task task : scheduledTasks) {
            if (task.getNextExecution() != null && 
                task.getNextExecution().isBefore(LocalDateTime.now())) {
                
                executeTask(task);
            }
        }
    }
    
    @Transactional
    public void executeTask(Task task) {
        log.info("Executing task: {}", task.getName());
        
        // Mark as running
        task.setStatus(TaskStatus.RUNNING);
        taskRepository.save(task);
        
        // Create execution record
        TaskExecution execution = TaskExecution.builder()
            .taskId(task.getId())
            .status(ExecutionStatus.RUNNING)
            .build();
        execution = executionRepository.save(execution);
        
        // Simulate execution (random success/failure)
        try {
            Thread.sleep(random.nextInt(2000) + 1000); // 1-3 seconds
            
            boolean success = random.nextDouble() > 0.1; // 90% success rate
            
            if (success) {
                completeTaskExecution(task, execution, true, null);
            } else {
                completeTaskExecution(task, execution, false, "Simulated failure");
            }
            
        } catch (InterruptedException e) {
            completeTaskExecution(task, execution, false, e.getMessage());
        }
    }
    
    @Transactional
    public void completeTaskExecution(Task task, TaskExecution execution, 
                                       boolean success, String errorMessage) {
        LocalDateTime completedAt = LocalDateTime.now();
        Long executionTime = java.time.Duration.between(
            execution.getStartedAt(), completedAt).toMillis();
        
        // Update execution record
        execution.setCompletedAt(completedAt);
        execution.setExecutionTimeMs(executionTime);
        execution.setStatus(success ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED);
        execution.setErrorMessage(errorMessage);
        executionRepository.save(execution);
        
        // Update task
        task.setLastExecution(completedAt);
        task.setExecutionCount(task.getExecutionCount() + 1);
        
        if (!success) {
            task.setFailureCount(task.getFailureCount() + 1);
            task.setStatus(TaskStatus.FAILED);
        } else {
            task.setStatus(TaskStatus.COMPLETED);
            
            // Schedule next execution for recurring tasks
            if (task.getType() == TaskType.CRON || task.getType() == TaskType.FIXED_DELAY) {
                task.setNextExecution(LocalDateTime.now().plusMinutes(5));
                task.setStatus(TaskStatus.SCHEDULED);
            }
        }
        
        // Update average execution time
        if (task.getAvgExecutionTimeMs() == null) {
            task.setAvgExecutionTimeMs(executionTime);
        } else {
            task.setAvgExecutionTimeMs(
                (task.getAvgExecutionTimeMs() + executionTime) / 2
            );
        }
        
        taskRepository.save(task);
        
        log.info("Task {} completed with status: {}", task.getName(), 
                 success ? "SUCCESS" : "FAILED");
    }
    
    public TaskMetrics getMetrics() {
        Long totalTasks = taskRepository.count();
        Long runningTasks = taskRepository.countByStatus(TaskStatus.RUNNING);
        Long failedTasks = taskRepository.countByStatus(TaskStatus.FAILED);
        Long totalExecutions = taskRepository.getTotalExecutions();
        Double avgExecutionTime = taskRepository.getAverageExecutionTime();
        
        Long successfulExecutions = totalExecutions - failedTasks;
        Double successRate = totalExecutions > 0 ? 
            (successfulExecutions.doubleValue() / totalExecutions * 100) : 100.0;
        
        return TaskMetrics.builder()
            .totalTasks(totalTasks)
            .runningTasks(runningTasks)
            .failedTasks(failedTasks)
            .totalExecutions(totalExecutions)
            .avgExecutionTimeMs(avgExecutionTime != null ? avgExecutionTime.longValue() : 0L)
            .successRate(successRate)
            .build();
    }
}
