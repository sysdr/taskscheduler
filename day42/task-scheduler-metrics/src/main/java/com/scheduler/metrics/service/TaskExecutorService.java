package com.scheduler.metrics.service;

import com.scheduler.metrics.model.Task;
import com.scheduler.metrics.model.TaskStatus;
import com.scheduler.metrics.repository.TaskRepository;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskExecutorService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutorService.class);
    
    private final TaskRepository taskRepository;
    private final TaskMetricsService metricsService;
    private final Random random = new Random();
    
    public TaskExecutorService(TaskRepository taskRepository, 
                                TaskMetricsService metricsService) {
        this.taskRepository = taskRepository;
        this.metricsService = metricsService;
    }
    
    @Transactional
    public Task submitTask(String name, String type, String priority) {
        Task task = new Task(name, type, priority);
        task = taskRepository.save(task);
        metricsService.recordTaskSubmitted(task);
        
        // Execute asynchronously
        executeTaskAsync(task.getId());
        
        return task;
    }
    
    private void executeTaskAsync(Long taskId) {
        CompletableFuture.runAsync(() -> {
            try {
                executeTask(taskId);
            } catch (Exception e) {
                logger.error("Async task execution failed for task {}", taskId, e);
            }
        });
    }
    
    @Transactional
    public void executeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
        
        Timer.Sample timerSample = metricsService.startTimer();
        
        // Update status to executing
        task.setStatus(TaskStatus.EXECUTING);
        task.setStartedAt(Instant.now());
        taskRepository.save(task);
        metricsService.recordTaskStarted(task);
        
        try {
            // Simulate task execution with variable duration
            int duration = simulateTaskExecution(task);
            
            // Complete task
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(Instant.now());
            task.setExecutionTimeMs((long) duration);
            taskRepository.save(task);
            
            metricsService.recordTaskCompleted(task, timerSample);
            
        } catch (Exception e) {
            // Handle failure
            task.setStatus(TaskStatus.FAILED);
            task.setCompletedAt(Instant.now());
            task.setErrorMessage(e.getMessage());
            task.setExecutionTimeMs(
                    Instant.now().toEpochMilli() - task.getStartedAt().toEpochMilli());
            taskRepository.save(task);
            
            metricsService.recordTaskFailed(task, timerSample, e.getMessage());
        }
    }
    
    private int simulateTaskExecution(Task task) throws Exception {
        // Simulate different execution times based on type
        int baseDuration = switch (task.getType()) {
            case "email" -> 50;
            case "report" -> 200;
            case "notification" -> 30;
            case "export" -> 500;
            default -> 100;
        };
        
        // Add some variance
        int variance = random.nextInt(baseDuration);
        int duration = baseDuration + variance;
        
        // Simulate occasional failures (10% chance)
        if (random.nextInt(10) == 0) {
            Thread.sleep(duration / 2);
            throw new RuntimeException("Simulated task failure - timeout");
        }
        
        Thread.sleep(duration);
        return duration;
    }
    
    @Scheduled(fixedRate = 5000)
    public void processQueuedTasks() {
        List<Task> queuedTasks = taskRepository.findByStatus(TaskStatus.QUEUED);
        
        for (Task task : queuedTasks) {
            try {
                executeTask(task.getId());
            } catch (Exception e) {
                logger.error("Failed to process queued task {}", task.getId(), e);
            }
        }
    }
    
    @Transactional
    public void retryTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
        
        if (task.getStatus() == TaskStatus.FAILED) {
            task.setStatus(TaskStatus.QUEUED);
            task.setRetryCount(task.getRetryCount() + 1);
            task.setErrorMessage(null);
            taskRepository.save(task);
            
            metricsService.recordRetry(task);
            metricsService.recordTaskSubmitted(task);
        }
    }
}
