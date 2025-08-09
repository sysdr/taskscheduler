package com.scheduler.service;

import com.scheduler.model.TaskDefinition;
import com.scheduler.model.TaskExecution;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskDefinitionRepository;
import com.scheduler.repository.TaskExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskExecutorService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutorService.class);
    
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    
    public TaskExecutorService(TaskDefinitionRepository taskDefinitionRepository,
                             TaskExecutionRepository taskExecutionRepository) {
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskExecutionRepository = taskExecutionRepository;
    }
    
    @Async
    @Transactional
    public CompletableFuture<Void> executeTask(TaskDefinition taskDef, TaskExecution execution) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Executing task: {} with class: {}", taskDef.getName(), taskDef.getTaskClass());
            
            // Simulate task execution (replace with actual task logic)
            simulateTaskExecution(taskDef);
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Mark execution as completed
            execution.setStatus(TaskStatus.COMPLETED);
            execution.setCompletedAt(LocalDateTime.now());
            execution.setExecutionTimeMs(executionTime);
            taskExecutionRepository.save(execution);
            
            // Update task definition
            taskDef.setStatus(TaskStatus.ACTIVE);
            taskDef.setNextExecutionAt(calculateNextExecution(taskDef));
            taskDefinitionRepository.save(taskDef);
            
            logger.info("Task {} completed successfully in {}ms", taskDef.getName(), executionTime);
            
        } catch (Exception e) {
            logger.error("Task {} failed: {}", taskDef.getName(), e.getMessage(), e);
            
            // Mark execution as failed
            execution.setStatus(TaskStatus.FAILED);
            execution.setCompletedAt(LocalDateTime.now());
            execution.setErrorMessage(e.getMessage());
            execution.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            taskExecutionRepository.save(execution);
            
            // Update task definition
            taskDef.setStatus(TaskStatus.FAILED);
            taskDefinitionRepository.save(taskDef);
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    private void simulateTaskExecution(TaskDefinition taskDef) throws InterruptedException {
        // Simulate different execution times based on task type
        int sleepTime = switch (taskDef.getTaskClass()) {
            case "FAST_TASK" -> 1000;
            case "MEDIUM_TASK" -> 5000;
            case "SLOW_TASK" -> 10000;
            default -> 2000;
        };
        
        Thread.sleep(sleepTime);
        
        // Simulate occasional failures (10% chance)
        if (Math.random() < 0.1) {
            throw new RuntimeException("Simulated task failure for testing");
        }
    }
    
    private LocalDateTime calculateNextExecution(TaskDefinition taskDef) {
        // Simple next execution calculation (in real implementation, use proper cron parser)
        return LocalDateTime.now().plusMinutes(5);
    }
}
