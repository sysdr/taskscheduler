package com.taskscheduler.service;

import com.taskscheduler.exception.PermanentTaskException;
import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskResult;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.model.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TaskExecutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionService.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Async
    public CompletableFuture<TaskResult> executeTask(Task task) {
        logger.info("Starting execution of task: {} (Type: {})", task.getId(), task.getType());
        
        task.setStatus(TaskStatus.RUNNING);
        taskRepository.save(task);
        
        try {
            TaskResult result = delegateToSpecificService(task);
            
            if (result.isSuccess()) {
                task.setStatus(TaskStatus.COMPLETED);
                logger.info("Task {} completed successfully", task.getId());
            } else {
                task.setStatus(TaskStatus.FAILED);
                task.setLastError(result.getMessage());
                task.incrementRetryCount();
                logger.error("Task {} failed: {}", task.getId(), result.getMessage());
            }
            
            taskRepository.save(task);
            return CompletableFuture.completedFuture(result);
            
        } catch (PermanentTaskException e) {
            // Don't retry permanent failures
            task.setStatus(TaskStatus.DEAD_LETTER);
            task.setLastError(e.getMessage());
            taskRepository.save(task);
            
            logger.error("Task {} failed permanently: {}", task.getId(), e.getMessage());
            return CompletableFuture.completedFuture(
                TaskResult.failure("Permanent failure - moved to dead letter", e)
            );
        } catch (Exception e) {
            task.setStatus(TaskStatus.FAILED);
            task.setLastError(e.getMessage());
            task.incrementRetryCount();
            taskRepository.save(task);
            
            logger.error("Task {} failed with exception: {}", task.getId(), e.getMessage());
            return CompletableFuture.completedFuture(
                TaskResult.failure("Task execution failed", e)
            );
        }
    }
    
    private TaskResult delegateToSpecificService(Task task) {
        return switch (task.getType()) {
            case EMAIL_NOTIFICATION -> emailService.sendEmail(task);
            case DATABASE_CLEANUP -> simulateDatabaseCleanup(task);
            case API_SYNC -> simulateApiSync(task);
            case FILE_PROCESSING -> simulateFileProcessing(task);
            case REPORT_GENERATION -> simulateReportGeneration(task);
        };
    }
    
    private TaskResult simulateDatabaseCleanup(Task task) {
        logger.info("Simulating database cleanup for task: {}", task.getId());
        return TaskResult.success("Database cleanup completed");
    }
    
    private TaskResult simulateApiSync(Task task) {
        logger.info("Simulating API sync for task: {}", task.getId());
        return TaskResult.success("API sync completed");
    }
    
    private TaskResult simulateFileProcessing(Task task) {
        logger.info("Simulating file processing for task: {}", task.getId());
        return TaskResult.success("File processing completed");
    }
    
    private TaskResult simulateReportGeneration(Task task) {
        logger.info("Simulating report generation for task: {}", task.getId());
        return TaskResult.success("Report generation completed");
    }
}
