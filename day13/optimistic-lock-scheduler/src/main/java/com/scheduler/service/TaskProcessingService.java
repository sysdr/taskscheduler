package com.scheduler.service;

import com.scheduler.entity.Task;
import com.scheduler.exception.TaskLockException;
import com.scheduler.repository.TaskRepository;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TaskProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessingService.class);
    
    private final TaskRepository taskRepository;
    private final MetricsService metricsService;
    private final String processorId;
    
    @Value("${scheduler.task.processor.retry.max-attempts:3}")
    private int maxRetryAttempts;
    
    @Value("${scheduler.task.processor.retry.delay-ms:100}")
    private long baseDelayMs;
    
    @Value("${scheduler.task.processor.retry.multiplier:2.0}")
    private double delayMultiplier;
    
    public TaskProcessingService(TaskRepository taskRepository, MetricsService metricsService) {
        this.taskRepository = taskRepository;
        this.metricsService = metricsService;
        this.processorId = generateProcessorId();
        logger.info("TaskProcessingService initialized with processorId: {}", processorId);
    }
    
    private String generateProcessorId() {
        return "processor-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Transactional
    public void processPendingTasks() {
        logger.debug("Looking for available tasks to process");
        
        List<Task> availableTasks = taskRepository.findAvailableTasksForProcessing(
            LocalDateTime.now(), 
            PageRequest.of(0, 10)
        );
        
        logger.debug("Found {} available tasks", availableTasks.size());
        
        for (Task task : availableTasks) {
            try {
                processTaskWithOptimisticLocking(task);
            } catch (TaskLockException e) {
                logger.debug("Could not acquire lock for task {}: {}", task.getId(), e.getMessage());
                metricsService.incrementOptimisticLockConflicts();
            } catch (Exception e) {
                logger.error("Unexpected error processing task {}", task.getId(), e);
            }
        }
    }
    
    private void processTaskWithOptimisticLocking(Task initialTask) {
        int attempt = 0;
        Task currentTask = initialTask;
        
        while (attempt < maxRetryAttempts) {
            try {
                attempt++;
                logger.debug("Attempting to process task {} (attempt {}/{})", 
                           currentTask.getId(), attempt, maxRetryAttempts);
                
                if (claimAndProcessTask(currentTask)) {
                    return; // Success!
                }
                
                if (attempt < maxRetryAttempts) {
                    // Exponential backoff with jitter
                    long delay = (long) (baseDelayMs * Math.pow(delayMultiplier, attempt - 1));
                    delay += ThreadLocalRandom.current().nextLong(0, delay / 2); // Add jitter
                    
                    logger.debug("Retrying task {} after {}ms delay", currentTask.getId(), delay);
                    Thread.sleep(delay);
                    
                    // Refresh task data for next attempt
                    final Long taskId = currentTask.getId();
                    Task refreshedTask = taskRepository.findById(taskId)
                        .orElseThrow(() -> new TaskLockException(taskId, processorId, 
                                                               "Task no longer exists"));
                    currentTask = refreshedTask;
                }
                
            } catch (OptimisticLockingFailureException e) {
                logger.debug("Optimistic lock conflict for task {} on attempt {}", 
                           currentTask.getId(), attempt);
                metricsService.incrementOptimisticLockConflicts();
                
                if (attempt == maxRetryAttempts) {
                    throw new TaskLockException(currentTask.getId(), processorId, 
                                              "Failed to acquire lock after " + maxRetryAttempts + " attempts", e);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new TaskLockException(currentTask.getId(), processorId, "Interrupted while waiting to retry", e);
            }
        }
        
        throw new TaskLockException(currentTask.getId(), processorId, 
                                  "Failed to process task after " + maxRetryAttempts + " attempts");
    }
    
    @Transactional
    private boolean claimAndProcessTask(Task task) {
        // Attempt to claim the task with optimistic locking
        LocalDateTime now = LocalDateTime.now();
        int updatedRows = taskRepository.claimTaskOptimistically(
            task.getId(),
            task.getVersion(),
            processorId,
            now,
            now
        );
        
        if (updatedRows == 0) {
            logger.debug("Failed to claim task {} - version conflict or status changed", task.getId());
            return false;
        }
        
        logger.info("Successfully claimed task {} for processing", task.getId());
        metricsService.incrementOptimisticLockSuccess();
        
        // Process the task
        Timer.Sample sample = metricsService.startProcessingTimer();
        try {
            executeTask(task);
            markTaskCompleted(task);
            metricsService.incrementTasksProcessed();
            logger.info("Successfully completed task {}", task.getId());
            return true;
            
        } catch (Exception e) {
            logger.error("Error executing task {}", task.getId(), e);
            markTaskFailed(task, e.getMessage());
            metricsService.incrementTasksFailed();
            return true; // We handled the task, even though it failed
        } finally {
            metricsService.stopProcessingTimer(sample);
        }
    }
    
    private void executeTask(Task task) throws Exception {
        logger.info("Executing task: {} - {}", task.getId(), task.getName());
        
        // Simulate task processing with random duration and potential failure
        int processingTimeMs = ThreadLocalRandom.current().nextInt(1000, 5000);
        
        for (int i = 0; i < processingTimeMs / 100; i++) {
            Thread.sleep(100);
            
            // Simulate random failures (5% chance)
            if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                throw new RuntimeException("Simulated task execution failure");
            }
        }
        
        logger.debug("Task {} execution completed in {}ms", task.getId(), processingTimeMs);
    }
    
    @Transactional
    private void markTaskCompleted(Task task) {
        LocalDateTime now = LocalDateTime.now();
        int updatedRows = taskRepository.markTaskCompleted(
            task.getId(),
            task.getVersion() + 1, // Version was incremented when we claimed it
            processorId,
            now,
            now
        );
        
        if (updatedRows == 0) {
            logger.warn("Failed to mark task {} as completed - version conflict", task.getId());
        }
    }
    
    @Transactional
    private void markTaskFailed(Task task, String errorMessage) {
        LocalDateTime now = LocalDateTime.now();
        
        // Determine if we should retry or mark as permanently failed
        boolean shouldRetry = task.getRetryCount() + 1 < task.getMaxRetries();
        Task.TaskStatus newStatus = shouldRetry ? Task.TaskStatus.RETRYING : Task.TaskStatus.FAILED;
        
        int updatedRows = taskRepository.markTaskFailed(
            task.getId(),
            task.getVersion() + 1, // Version was incremented when we claimed it
            processorId,
            newStatus,
            errorMessage,
            1, // Increment retry count
            now
        );
        
        if (updatedRows == 0) {
            logger.warn("Failed to mark task {} as failed - version conflict", task.getId());
        } else {
            logger.info("Marked task {} as {} (retry count: {})", 
                       task.getId(), newStatus, task.getRetryCount() + 1);
        }
    }
    
    public String getProcessorId() {
        return processorId;
    }
}
