package com.taskscheduler.service;

import com.taskscheduler.entity.TaskExecution;
import com.taskscheduler.enums.TaskStatus;
import com.taskscheduler.repository.TaskExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing task state transitions with validation
 */
@Service
public class StateTransitionService {
    
    private static final Logger logger = LoggerFactory.getLogger(StateTransitionService.class);
    
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    
    @Autowired
    private TaskMetricsService metricsService;
    
    /**
     * Safely transition task to RUNNING status
     */
    @Transactional
    public TaskExecution startTask(Long taskId) {
        TaskExecution task = getTaskExecution(taskId);
        
        logger.info("Attempting to start task {} with current status {}", taskId, task.getStatus());
        
        if (!task.getStatus().canTransitionTo(TaskStatus.RUNNING)) {
            throw new IllegalStateException(
                String.format("Task %d cannot transition from %s to RUNNING", taskId, task.getStatus())
            );
        }
        
        task.start();
        TaskExecution savedTask = taskExecutionRepository.save(task);
        
        // Update metrics
        metricsService.recordStatusTransition(TaskStatus.PENDING, TaskStatus.RUNNING);
        
        logger.info("Task {} successfully started", taskId);
        return savedTask;
    }
    
    /**
     * Safely transition task to SUCCEEDED status
     */
    @Transactional
    public TaskExecution completeTask(Long taskId) {
        TaskExecution task = getTaskExecution(taskId);
        
        logger.info("Attempting to complete task {} with current status {}", taskId, task.getStatus());
        
        if (!task.getStatus().canTransitionTo(TaskStatus.SUCCEEDED)) {
            throw new IllegalStateException(
                String.format("Task %d cannot transition from %s to SUCCEEDED", taskId, task.getStatus())
            );
        }
        
        TaskStatus previousStatus = task.getStatus();
        task.succeed();
        TaskExecution savedTask = taskExecutionRepository.save(task);
        
        // Update metrics
        metricsService.recordStatusTransition(previousStatus, TaskStatus.SUCCEEDED);
        metricsService.recordTaskCompletion(savedTask.getDurationMs());
        
        logger.info("Task {} successfully completed in {}ms", taskId, savedTask.getDurationMs());
        return savedTask;
    }
    
    /**
     * Safely transition task to FAILED status
     */
    @Transactional
    public TaskExecution failTask(Long taskId, String errorMessage) {
        TaskExecution task = getTaskExecution(taskId);
        
        logger.info("Attempting to fail task {} with current status {}", taskId, task.getStatus());
        
        if (!task.getStatus().canTransitionTo(TaskStatus.FAILED)) {
            throw new IllegalStateException(
                String.format("Task %d cannot transition from %s to FAILED", taskId, task.getStatus())
            );
        }
        
        TaskStatus previousStatus = task.getStatus();
        task.fail(errorMessage);
        TaskExecution savedTask = taskExecutionRepository.save(task);
        
        // Update metrics
        metricsService.recordStatusTransition(previousStatus, TaskStatus.FAILED);
        metricsService.recordTaskFailure();
        
        logger.error("Task {} failed: {}", taskId, errorMessage);
        return savedTask;
    }
    
    /**
     * Create a new task execution
     */
    @Transactional
    public TaskExecution createTask(String taskName, String executionDetails) {
        TaskExecution task = new TaskExecution(taskName, executionDetails);
        TaskExecution savedTask = taskExecutionRepository.save(task);
        
        metricsService.recordTaskCreation();
        
        logger.info("Created new task {} with ID {}", taskName, savedTask.getId());
        return savedTask;
    }
    
    /**
     * Get task execution by ID
     */
    public TaskExecution getTaskExecution(Long taskId) {
        Optional<TaskExecution> task = taskExecutionRepository.findById(taskId);
        if (task.isEmpty()) {
            throw new IllegalArgumentException("Task not found with ID: " + taskId);
        }
        return task.get();
    }
    
    /**
     * Validate state transition without performing it
     */
    public boolean isValidTransition(Long taskId, TaskStatus targetStatus) {
        TaskExecution task = getTaskExecution(taskId);
        return task.getStatus().canTransitionTo(targetStatus);
    }
    
    /**
     * Clean up stale running tasks (zombie detection)
     */
    @Transactional
    public int cleanupStaleRunningTasks(int staleTimeoutMinutes) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(staleTimeoutMinutes);
        var staleTasks = taskExecutionRepository.findStaleTasksByStatus(TaskStatus.RUNNING, cutoffTime);
        
        int cleanedCount = 0;
        for (TaskExecution staleTask : staleTasks) {
            failTask(staleTask.getId(), "Task timed out - marked as failed by cleanup process");
            cleanedCount++;
        }
        
        if (cleanedCount > 0) {
            logger.warn("Cleaned up {} stale running tasks older than {} minutes", 
                       cleanedCount, staleTimeoutMinutes);
        }
        
        return cleanedCount;
    }
}
