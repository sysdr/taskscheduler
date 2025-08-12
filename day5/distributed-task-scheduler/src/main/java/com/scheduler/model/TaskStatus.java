package com.scheduler.model;

public enum TaskStatus {
    ACTIVE("Active - Ready for execution"),
    INACTIVE("Inactive - Temporarily disabled"),
    RUNNING("Running - Currently executing"),
    FAILED("Failed - Last execution failed"),
    COMPLETED("Completed - Successfully executed");
    
    private final String description;
    
    TaskStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
