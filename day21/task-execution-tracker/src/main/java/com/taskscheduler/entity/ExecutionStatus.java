package com.taskscheduler.entity;

public enum ExecutionStatus {
    PENDING("Task is waiting to be executed"),
    RUNNING("Task is currently executing"),
    SUCCESS("Task completed successfully"),
    FAILED("Task execution failed"),
    TIMEOUT("Task execution timed out"),
    CANCELLED("Task execution was cancelled");
    
    private final String description;
    
    ExecutionStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
