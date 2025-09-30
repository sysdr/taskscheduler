package com.taskscheduler.service;

import com.taskscheduler.config.RetryPolicy;
import com.taskscheduler.exception.NonRetriableTaskException;
import com.taskscheduler.exception.RetriableTaskException;
import com.taskscheduler.exception.TaskException;
import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskExecutionResult;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TaskExecutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionService.class);
    private final TaskRepository taskRepository;
    private final RetryPolicy retryPolicy;
    private final Random random = new Random();
    
    @Autowired
    public TaskExecutionService(TaskRepository taskRepository, RetryPolicy retryPolicy) {
        this.taskRepository = taskRepository;
        this.retryPolicy = retryPolicy;
    }
    
    @Transactional
    public TaskExecutionResult executeTask(Task task) {
        logger.info("Executing task: {} (attempt {}/{})", task.getName(), 
                   task.getAttemptCount() + 1, task.getMaxRetries());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Update task status
            task.setStatus(TaskStatus.RUNNING);
            task.setStartedTime(LocalDateTime.now());
            task.incrementAttemptCount();
            taskRepository.save(task);
            
            // Execute the actual task logic
            performTaskExecution(task);
            
            // Mark as completed
            long executionTime = System.currentTimeMillis() - startTime;
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedTime(LocalDateTime.now());
            task.setExecutionTimeMs(executionTime);
            task.setErrorMessage(null);
            taskRepository.save(task);
            
            logger.info("Task completed successfully: {} in {}ms", task.getName(), executionTime);
            return TaskExecutionResult.success("Task completed successfully", executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return handleTaskFailure(task, e, executionTime);
        }
    }
    
    private void performTaskExecution(Task task) throws TaskException {
        // Simulate different types of task execution based on task type
        switch (task.getTaskType().toLowerCase()) {
            case "email":
                simulateEmailTask(task);
                break;
            case "payment":
                simulatePaymentTask(task);
                break;
            case "report":
                simulateReportTask(task);
                break;
            case "cleanup":
                simulateCleanupTask(task);
                break;
            default:
                simulateGenericTask(task);
        }
    }
    
    private void simulateEmailTask(Task task) throws TaskException {
        // Simulate network timeouts (retriable)
        if (random.nextDouble() < 0.3) {
            throw new RetriableTaskException("Email service temporarily unavailable");
        }
        
        // Simulate invalid email addresses (non-retriable)
        if (task.getTaskData() != null && task.getTaskData().contains("invalid")) {
            throw new NonRetriableTaskException("Invalid email address format");
        }
        
        // Simulate processing time
        try {
            Thread.sleep(random.nextInt(2000) + 500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void simulatePaymentTask(Task task) throws TaskException {
        // Simulate payment gateway timeouts (retriable)
        if (random.nextDouble() < 0.25) {
            throw new RetriableTaskException("Payment gateway timeout");
        }
        
        // Simulate insufficient funds (non-retriable)
        if (task.getTaskData() != null && task.getTaskData().contains("insufficient")) {
            throw new NonRetriableTaskException("Insufficient funds");
        }
        
        // Simulate processing time
        try {
            Thread.sleep(random.nextInt(3000) + 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void simulateReportTask(Task task) throws TaskException {
        // Simulate database connection issues (retriable)
        if (random.nextDouble() < 0.2) {
            throw new RetriableTaskException("Database connection timeout");
        }
        
        // Simulate processing time
        try {
            Thread.sleep(random.nextInt(5000) + 2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void simulateCleanupTask(Task task) throws TaskException {
        // Simulate file system issues (retriable)
        if (random.nextDouble() < 0.15) {
            throw new RetriableTaskException("File system temporarily unavailable");
        }
        
        // Simulate processing time
        try {
            Thread.sleep(random.nextInt(1000) + 200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void simulateGenericTask(Task task) throws TaskException {
        // Generic task with occasional failures
        if (random.nextDouble() < 0.1) {
            throw new RetriableTaskException("Generic transient failure");
        }
        
        // Simulate processing time
        try {
            Thread.sleep(random.nextInt(1500) + 300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private TaskExecutionResult handleTaskFailure(Task task, Exception exception, long executionTime) {
        logger.error("Task execution failed: {} (attempt {}/{})", task.getName(), 
                    task.getAttemptCount(), task.getMaxRetries(), exception);
        
        task.setExecutionTimeMs(executionTime);
        task.setErrorMessage(exception.getMessage());
        
        // Determine if the task should be retried
        boolean isRetriable = isExceptionRetriable(exception);
        boolean canRetry = task.canRetry() && isRetriable;
        
        if (canRetry) {
            scheduleRetry(task);
            logger.info("Task scheduled for retry: {} (next attempt in {}ms)", 
                       task.getName(), 
                       task.getNextRetryTime() != null ? 
                       java.time.Duration.between(LocalDateTime.now(), task.getNextRetryTime()).toMillis() : 0);
        } else {
            task.setStatus(TaskStatus.FAILED);
            logger.error("Task permanently failed: {} ({})", task.getName(), 
                        canRetry ? "Non-retriable exception" : "Max retries exceeded");
        }
        
        taskRepository.save(task);
        return TaskExecutionResult.failure(exception.getMessage(), exception, executionTime);
    }
    
    private boolean isExceptionRetriable(Exception exception) {
        if (exception instanceof TaskException) {
            return ((TaskException) exception).isRetriable();
        }
        
        // Check for common retriable exceptions
        String message = exception.getMessage().toLowerCase();
        return message.contains("timeout") ||
               message.contains("connection") ||
               message.contains("temporarily") ||
               message.contains("unavailable") ||
               message.contains("network");
    }
    
    private void scheduleRetry(Task task) {
        long delayMs = retryPolicy.calculateBackoffDelay(task.getAttemptCount() - 1);
        LocalDateTime nextRetryTime = LocalDateTime.now().plusNanos(delayMs * 1_000_000);
        
        task.setStatus(TaskStatus.RETRYING);
        task.setNextRetryTime(nextRetryTime);
        
        logger.debug("Calculated retry delay: {}ms for task: {}", delayMs, task.getName());
    }
}
