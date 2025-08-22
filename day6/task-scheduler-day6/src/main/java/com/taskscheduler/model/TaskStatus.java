package com.taskscheduler.model;

/**
 * Represents the various states a task definition can be in.
 * Used for lifecycle management and coordination in distributed scheduling.
 */
public enum TaskStatus {
    ACTIVE("Task is active and ready to be scheduled"),
    PAUSED("Task is temporarily disabled"),
    COMPLETED("One-time task has finished execution"),
    FAILED("Task failed and requires attention"),
    ARCHIVED("Task is soft deleted");
    
    private final String description;
    
    TaskStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
