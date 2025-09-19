package com.taskscheduler.service;

import com.taskscheduler.entity.TaskExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskSimulatorService {
    
    @Autowired
    private TaskExecutionService executionService;
    
    private final Random random = new Random();
    
    @Scheduled(fixedDelay = 5000) // Run every 5 seconds
    public void simulateRandomTasks() {
        // Create different types of tasks
        String[] taskTypes = {
            "Email Notification", "Data Processing", "Report Generation",
            "File Cleanup", "Database Backup", "User Analytics"
        };
        
        String taskType = taskTypes[random.nextInt(taskTypes.length)];
        String description = "Simulated " + taskType + " task #" + random.nextInt(1000);
        
        TaskExecution execution = executionService.createExecution(taskType, description);
        
        // Simulate task execution asynchronously
        executeSimulatedTask(execution.getExecutionId());
    }
    
    @Async
    public CompletableFuture<Void> executeSimulatedTask(String executionId) {
        try {
            // Start the execution
            executionService.startExecution(executionId);
            
            // Simulate work with random duration
            int duration = 1000 + random.nextInt(5000); // 1-6 seconds
            Thread.sleep(duration);
            
            // Randomly succeed or fail (90% success rate)
            if (random.nextDouble() < 0.9) {
                executionService.completeExecution(executionId);
            } else {
                String errorMessage = "Simulated failure: " + getRandomError();
                String stackTrace = "java.lang.RuntimeException: " + errorMessage + "\n\tat com.example.Task.execute(Task.java:42)";
                executionService.failExecution(executionId, errorMessage, stackTrace);
            }
            
        } catch (InterruptedException e) {
            String errorMessage = "Task interrupted: " + e.getMessage();
            String stackTrace = e.getClass().getName() + ": " + errorMessage;
            executionService.failExecution(executionId, errorMessage, stackTrace);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            String errorMessage = "Unexpected error: " + e.getMessage();
            String stackTrace = e.getClass().getName() + ": " + errorMessage;
            executionService.failExecution(executionId, errorMessage, stackTrace);
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    private String getRandomError() {
        String[] errors = {
            "Network timeout", "Database connection failed", "Insufficient memory",
            "File not found", "Permission denied", "Service unavailable"
        };
        return errors[random.nextInt(errors.length)];
    }
}
