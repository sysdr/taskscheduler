package com.taskscheduler.enums;

import java.util.Set;

/**
 * Task execution status with state machine validation
 */
public enum TaskStatus {
    PENDING("Task is queued and waiting for execution"),
    RUNNING("Task is currently being executed"),
    SUCCEEDED("Task completed successfully"),
    FAILED("Task failed during execution");
    
    private final String description;
    
    TaskStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Validates if transition from current status to new status is allowed
     * 
     * @param newStatus Target status
     * @return true if transition is valid
     */
    public boolean canTransitionTo(TaskStatus newStatus) {
        if (this == newStatus) {
            return false; // No self-transitions allowed
        }
        
        return switch(this) {
            case PENDING -> newStatus == RUNNING;
            case RUNNING -> newStatus == SUCCEEDED || newStatus == FAILED;
            case SUCCEEDED, FAILED -> false; // Terminal states
        };
    }
    
    /**
     * Get all valid next statuses from current status
     * 
     * @return Set of valid next statuses
     */
    public Set<TaskStatus> getValidNextStatuses() {
        return switch(this) {
            case PENDING -> Set.of(RUNNING);
            case RUNNING -> Set.of(SUCCEEDED, FAILED);
            case SUCCEEDED, FAILED -> Set.of(); // Terminal states
        };
    }
    
    /**
     * Check if this is a terminal status (no further transitions allowed)
     * 
     * @return true if terminal status
     */
    public boolean isTerminal() {
        return this == SUCCEEDED || this == FAILED;
    }
    
    /**
     * Check if this is an active status (task is being processed)
     * 
     * @return true if active status
     */
    public boolean isActive() {
        return this == RUNNING;
    }
    
    /**
     * Check if this is a completed status (success or failure)
     * 
     * @return true if completed status
     */
    public boolean isCompleted() {
        return this == SUCCEEDED || this == FAILED;
    }
}
