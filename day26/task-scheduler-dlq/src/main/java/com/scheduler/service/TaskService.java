package com.scheduler.service;

import com.scheduler.exception.TaskProcessingException;
import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final Random random = new Random();
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @Autowired
    private DeadLetterService deadLetterService;
    
    public Task createTask(Task task) {
        logger.info("Creating task: {}", task.getName());
        meterRegistry.counter("tasks.created").increment();
        return taskRepository.save(task);
    }
    
    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
    
    @Retryable(
        retryFor = {TaskProcessingException.class},
        maxAttemptsExpression = "#{@task.maxRetries + 1}",
        backoff = @Backoff(delayExpression = "#{1000}", multiplierExpression = "#{2}")
    )
    @Async
    public void processTask(Task task) throws TaskProcessingException {
        logger.info("Processing task: {} (attempt {})", task.getId(), task.getRetryCount() + 1);
        
        // Update task status to processing
        task.setStatus(TaskStatus.PROCESSING);
        task.incrementRetryCount();
        taskRepository.save(task);
        
        try {
            // Simulate task processing with various failure scenarios
            simulateTaskExecution(task);
            
            // Mark as completed
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
            
            meterRegistry.counter("tasks.completed").increment();
            logger.info("Task {} completed successfully", task.getId());
            
        } catch (Exception e) {
            logger.warn("Task {} failed on attempt {}: {}", task.getId(), task.getRetryCount(), e.getMessage());
            
            task.setStatus(TaskStatus.RETRYING);
            task.setLastError(e.getMessage());
            taskRepository.save(task);
            
            meterRegistry.counter("tasks.failed", "attempt", String.valueOf(task.getRetryCount())).increment();
            throw new TaskProcessingException(e.getMessage(), task.getId(), true, e);
        }
    }
    
    @Recover
    public void recoverFromFailure(TaskProcessingException ex, Task task) {
        logger.error("Task {} exhausted all retry attempts. Moving to dead letter queue", ex.getTaskId());
        
        // Reload task from database to get latest state
        Optional<Task> latestTask = taskRepository.findById(ex.getTaskId());
        if (latestTask.isPresent()) {
            deadLetterService.moveToDeadLetter(latestTask.get(), ex);
        }
        
        meterRegistry.counter("tasks.dead_lettered").increment();
    }
    
    @Scheduled(fixedDelay = 10000) // Run every 10 seconds
    public void processScheduledTasks() {
        List<Task> readyTasks = taskRepository.findReadyTasks(TaskStatus.CREATED, LocalDateTime.now());
        
        logger.debug("Found {} tasks ready for processing", readyTasks.size());
        
        for (Task task : readyTasks) {
            try {
                processTask(task);
            } catch (Exception e) {
                logger.error("Error initiating task processing for task {}: {}", task.getId(), e.getMessage());
            }
        }
    }
    
    @Scheduled(fixedDelay = 60000) // Run every minute
    public void cleanupFailedTasks() {
        List<Task> failedTasks = taskRepository.findFailedTasks();
        
        for (Task task : failedTasks) {
            if (task.getRetryCount() >= task.getMaxRetries()) {
                logger.info("Moving failed task {} to dead letter queue", task.getId());
                deadLetterService.moveToDeadLetter(task, 
                    new RuntimeException("Task exceeded maximum retry attempts: " + task.getRetryCount()));
            }
        }
    }
    
    private void simulateTaskExecution(Task task) throws Exception {
        // Simulate processing time
        Thread.sleep(1000 + random.nextInt(2000));
        
        // Simulate different failure scenarios based on task name
        String taskName = task.getName().toLowerCase();
        
        if (taskName.contains("timeout")) {
            throw new RuntimeException("Task execution timeout after 30 seconds");
        }
        
        if (taskName.contains("network")) {
            throw new RuntimeException("Network connection failed: Unable to reach external service");
        }
        
        if (taskName.contains("validation")) {
            throw new IllegalArgumentException("Validation failed: Invalid payload format");
        }
        
        if (taskName.contains("data")) {
            throw new RuntimeException("Invalid data: Required field 'id' is missing");
        }
        
        if (taskName.contains("fail") && random.nextDouble() < 0.7) {
            throw new RuntimeException("Random processing error: Service temporarily unavailable");
        }
        
        // Success case - process normally
        logger.debug("Task {} processed successfully", task.getId());
    }
    
    public long getTaskCount() {
        return taskRepository.count();
    }
    
    public long getTaskCountByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
}
