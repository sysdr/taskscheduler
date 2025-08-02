package com.taskscheduler.model;

import java.time.LocalDateTime;

public class TaskExecution {
    private String taskName;
    private String executionType;
    private LocalDateTime executionTime;
    private String status;
    private long executionDuration;
    
    public TaskExecution(String taskName, String executionType, LocalDateTime executionTime, String status, long executionDuration) {
        this.taskName = taskName;
        this.executionType = executionType;
        this.executionTime = executionTime;
        this.status = status;
        this.executionDuration = executionDuration;
    }
    
    // Getters and Setters
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getExecutionType() { return executionType; }
    public void setExecutionType(String executionType) { this.executionType = executionType; }
    
    public LocalDateTime getExecutionTime() { return executionTime; }
    public void setExecutionTime(LocalDateTime executionTime) { this.executionTime = executionTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public long getExecutionDuration() { return executionDuration; }
    public void setExecutionDuration(long executionDuration) { this.executionDuration = executionDuration; }
}
