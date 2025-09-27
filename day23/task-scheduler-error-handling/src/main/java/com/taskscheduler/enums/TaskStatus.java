package com.taskscheduler.enums;

public enum TaskStatus {
    PENDING("Task is waiting to be executed"),
    RUNNING("Task is currently being executed"), 
    SUCCEEDED("Task completed successfully"),
    FAILED("Task execution failed");
    
    private final String description;
    
    TaskStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isTerminal() {
        return this == SUCCEEDED || this == FAILED;
    }
    
    public boolean isActive() {
        return this == RUNNING;
    }
}
