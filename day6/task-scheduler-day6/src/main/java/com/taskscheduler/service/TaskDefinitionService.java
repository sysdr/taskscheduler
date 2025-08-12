package com.taskscheduler.service;

import com.taskscheduler.model.TaskDefinition;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.repository.TaskDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for TaskDefinition business logic.
 * Handles task lifecycle, validation, and coordination for distributed scheduling.
 */
@Service
@Transactional
public class TaskDefinitionService {
    
    private final TaskDefinitionRepository taskDefinitionRepository;
    
    @Autowired
    public TaskDefinitionService(TaskDefinitionRepository taskDefinitionRepository) {
        this.taskDefinitionRepository = taskDefinitionRepository;
    }
    
    /**
     * Create a new task definition
     */
    public TaskDefinition createTask(TaskDefinition taskDefinition) {
        // Set initial next run time to current time for immediate scheduling
        if (taskDefinition.getNextRunTime() == null) {
            taskDefinition.setNextRunTime(LocalDateTime.now());
        }
        
        // Set created by if not specified
        if (taskDefinition.getCreatedBy() == null) {
            taskDefinition.setCreatedBy("system");
        }
        
        return taskDefinitionRepository.save(taskDefinition);
    }
    
    /**
     * Update an existing task definition
     */
    public Optional<TaskDefinition> updateTask(String id, TaskDefinition updates) {
        return taskDefinitionRepository.findById(id)
                .map(existingTask -> {
                    // Update only non-null fields
                    if (updates.getName() != null) {
                        existingTask.setName(updates.getName());
                    }
                    if (updates.getTaskType() != null) {
                        existingTask.setTaskType(updates.getTaskType());
                    }
                    if (updates.getCronExpression() != null) {
                        existingTask.setCronExpression(updates.getCronExpression());
                    }
                    if (updates.getPayload() != null) {
                        existingTask.setPayload(updates.getPayload());
                    }
                    if (updates.getPriority() != null) {
                        existingTask.setPriority(updates.getPriority());
                    }
                    if (updates.getTimeoutSeconds() != null) {
                        existingTask.setTimeoutSeconds(updates.getTimeoutSeconds());
                    }
                    
                    return taskDefinitionRepository.save(existingTask);
                });
    }
    
    /**
     * Find task by ID
     */
    @Transactional(readOnly = true)
    public Optional<TaskDefinition> findById(String id) {
        return taskDefinitionRepository.findById(id);
    }
    
    /**
     * Find all tasks
     */
    @Transactional(readOnly = true)
    public List<TaskDefinition> findAllTasks() {
        return taskDefinitionRepository.findAll();
    }
    
    /**
     * Find tasks eligible for execution
     */
    @Transactional(readOnly = true)
    public List<TaskDefinition> findEligibleTasks() {
        return taskDefinitionRepository.findEligibleTasks(TaskStatus.ACTIVE, LocalDateTime.now());
    }
    
    /**
     * Pause a task
     */
    public Optional<TaskDefinition> pauseTask(String id) {
        return taskDefinitionRepository.findById(id)
                .map(task -> {
                    task.pause();
                    return taskDefinitionRepository.save(task);
                });
    }
    
    /**
     * Resume a task
     */
    public Optional<TaskDefinition> resumeTask(String id) {
        return taskDefinitionRepository.findById(id)
                .map(task -> {
                    task.resume();
                    // Set next run time to now for immediate consideration
                    task.setNextRunTime(LocalDateTime.now());
                    return taskDefinitionRepository.save(task);
                });
    }
    
    /**
     * Mark task as completed
     */
    public Optional<TaskDefinition> markTaskCompleted(String id) {
        return taskDefinitionRepository.findById(id)
                .map(task -> {
                    task.markAsCompleted();
                    return taskDefinitionRepository.save(task);
                });
    }
    
    /**
     * Mark task as failed
     */
    public Optional<TaskDefinition> markTaskFailed(String id) {
        return taskDefinitionRepository.findById(id)
                .map(task -> {
                    task.markAsFailed();
                    return taskDefinitionRepository.save(task);
                });
    }
    
    /**
     * Delete a task (soft delete by archiving)
     */
    public boolean deleteTask(String id) {
        return taskDefinitionRepository.findById(id)
                .map(task -> {
                    task.setStatus(TaskStatus.ARCHIVED);
                    taskDefinitionRepository.save(task);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Get task statistics
     */
    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics() {
        long totalTasks = taskDefinitionRepository.count();
        long activeTasks = taskDefinitionRepository.countByStatus(TaskStatus.ACTIVE);
        long pausedTasks = taskDefinitionRepository.countByStatus(TaskStatus.PAUSED);
        long failedTasks = taskDefinitionRepository.countByStatus(TaskStatus.FAILED);
        long completedTasks = taskDefinitionRepository.countByStatus(TaskStatus.COMPLETED);
        
        return new TaskStatistics(totalTasks, activeTasks, pausedTasks, failedTasks, completedTasks);
    }
    
    /**
     * Find high priority tasks
     */
    @Transactional(readOnly = true)
    public List<TaskDefinition> findHighPriorityTasks() {
        return taskDefinitionRepository.findHighPriorityTasks();
    }
    
    /**
     * Find failed tasks that need attention
     */
    @Transactional(readOnly = true)
    public List<TaskDefinition> findFailedTasks() {
        return taskDefinitionRepository.findFailedTasks();
    }
    
    /**
     * Inner class for task statistics
     */
    public static class TaskStatistics {
        private final long totalTasks;
        private final long activeTasks;
        private final long pausedTasks;
        private final long failedTasks;
        private final long completedTasks;
        
        public TaskStatistics(long totalTasks, long activeTasks, long pausedTasks, 
                             long failedTasks, long completedTasks) {
            this.totalTasks = totalTasks;
            this.activeTasks = activeTasks;
            this.pausedTasks = pausedTasks;
            this.failedTasks = failedTasks;
            this.completedTasks = completedTasks;
        }
        
        // Getters
        public long getTotalTasks() { return totalTasks; }
        public long getActiveTasks() { return activeTasks; }
        public long getPausedTasks() { return pausedTasks; }
        public long getFailedTasks() { return failedTasks; }
        public long getCompletedTasks() { return completedTasks; }
    }
}
