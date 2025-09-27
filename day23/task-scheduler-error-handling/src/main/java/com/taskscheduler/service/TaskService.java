package com.taskscheduler.service;

import com.taskscheduler.dto.TaskDto;
import com.taskscheduler.entity.Task;
import com.taskscheduler.enums.TaskStatus;
import com.taskscheduler.exception.TaskExecutionException;
import com.taskscheduler.exception.TaskNotFoundException;
import com.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    public Task createTask(TaskDto taskDto) {
        logger.info("Creating new task: {}", taskDto.getName());
        
        Task task = new Task(taskDto.getName(), taskDto.getDescription());
        Task savedTask = taskRepository.save(task);
        
        logger.info("Task created successfully with ID: {}", savedTask.getId());
        return savedTask;
    }
    
    public Task getTaskById(Long id) {
        logger.debug("Fetching task with ID: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));
    }
    
    public Page<Task> getAllTasks(Pageable pageable) {
        logger.debug("Fetching all tasks with pagination");
        return taskRepository.findAll(pageable);
    }
    
    public List<Task> getTasksByStatus(TaskStatus status) {
        logger.debug("Fetching tasks with status: {}", status);
        return taskRepository.findByStatus(status);
    }
    
    @Async
    public CompletableFuture<Void> executeTaskAsync(Long taskId) {
        logger.info("Starting async execution of task ID: {}", taskId);
        
        try {
            executeTaskWithErrorHandling(taskId);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            logger.error("Async task execution failed for task ID: {}", taskId, e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public void executeTaskWithErrorHandling(Long taskId) {
        Task task = getTaskById(taskId);
        
        if (task.getStatus().isTerminal()) {
            logger.warn("Attempting to execute task {} that is already in terminal state: {}", 
                       taskId, task.getStatus());
            throw new TaskExecutionException("Cannot execute task in terminal state: " + task.getStatus());
        }
        
        // Mark task as running
        task.markAsRunning();
        taskRepository.save(task);
        logger.info("Task {} marked as RUNNING", taskId);
        
        try {
            // Execute the actual task logic with comprehensive error handling
            performTaskExecution(task);
            
            // Mark as succeeded if no exception occurred
            task.markAsSucceeded();
            taskRepository.save(task);
            logger.info("Task {} completed successfully", taskId);
            
        } catch (Exception e) {
            // Handle any exception that occurs during task execution
            handleTaskExecutionFailure(task, e);
        }
    }
    
    private void performTaskExecution(Task task) throws Exception {
        logger.info("Executing task: {} - {}", task.getId(), task.getName());
        
        // Simulate different types of task execution based on task name
        String taskName = task.getName().toLowerCase();
        
        if (taskName.contains("email")) {
            simulateEmailTask(task);
        } else if (taskName.contains("data")) {
            simulateDataProcessingTask(task);
        } else if (taskName.contains("report")) {
            simulateReportGenerationTask(task);
        } else if (taskName.contains("fail")) {
            simulateFailingTask(task);
        } else {
            simulateGenericTask(task);
        }
        
        logger.debug("Task execution completed for: {}", task.getName());
    }
    
    private void simulateEmailTask(Task task) throws Exception {
        logger.debug("Simulating email task execution");
        Thread.sleep(2000); // Simulate email sending delay
        
        // 10% chance of failure
        if (Math.random() < 0.1) {
            throw new RuntimeException("SMTP server connection failed");
        }
        
        logger.debug("Email sent successfully");
    }
    
    private void simulateDataProcessingTask(Task task) throws Exception {
        logger.debug("Simulating data processing task execution");
        Thread.sleep(5000); // Simulate data processing delay
        
        // 15% chance of failure
        if (Math.random() < 0.15) {
            throw new RuntimeException("Database connection timeout during data processing");
        }
        
        logger.debug("Data processing completed successfully");
    }
    
    private void simulateReportGenerationTask(Task task) throws Exception {
        logger.debug("Simulating report generation task execution");
        Thread.sleep(3000); // Simulate report generation delay
        
        // 5% chance of failure
        if (Math.random() < 0.05) {
            throw new RuntimeException("Insufficient memory for report generation");
        }
        
        logger.debug("Report generated successfully");
    }
    
    private void simulateFailingTask(Task task) throws Exception {
        logger.debug("Simulating intentionally failing task");
        Thread.sleep(1000);
        throw new RuntimeException("This task is designed to fail for testing error handling");
    }
    
    private void simulateGenericTask(Task task) throws Exception {
        logger.debug("Simulating generic task execution");
        Thread.sleep(1500); // Simulate generic processing delay
        
        // 8% chance of failure
        if (Math.random() < 0.08) {
            throw new RuntimeException("Generic task processing error");
        }
        
        logger.debug("Generic task completed successfully");
    }
    
    private void handleTaskExecutionFailure(Task task, Exception exception) {
        logger.error("Task {} execution failed", task.getId(), exception);
        
        // Extract error message and stack trace
        String errorMessage = exception.getMessage();
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = exception.getClass().getSimpleName();
        }
        
        String stackTrace = getStackTraceAsString(exception);
        
        // Mark task as failed with error details
        task.markAsFailed(errorMessage, stackTrace);
        taskRepository.save(task);
        
        logger.warn("Task {} marked as FAILED with error: {}", task.getId(), errorMessage);
    }
    
    private String getStackTraceAsString(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }
    
    public long getTaskCountByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
    
    public List<Task> findStuckTasks(int timeoutMinutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(timeoutMinutes);
        return taskRepository.findStuckTasks(threshold);
    }
    
    public void cleanupStuckTasks(int timeoutMinutes) {
        List<Task> stuckTasks = findStuckTasks(timeoutMinutes);
        
        for (Task task : stuckTasks) {
            logger.warn("Cleaning up stuck task: {} (started at: {})", task.getId(), task.getStartedAt());
            task.markAsFailed("Task timeout - exceeded " + timeoutMinutes + " minutes", 
                             "Task was marked as failed due to execution timeout");
            taskRepository.save(task);
        }
        
        if (!stuckTasks.isEmpty()) {
            logger.info("Cleaned up {} stuck tasks", stuckTasks.size());
        }
    }
}
