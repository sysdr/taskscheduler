package com.taskscheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;

public class Task {
    private String taskId;
    private String taskName;
    private String taskType;
    private TaskStatus status;
    private Map<String, Object> payload;
    private int priority;
    private int maxRetries;
    private int currentRetries;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executedAt;

    public Task() {
        this.createdAt = LocalDateTime.now();
        this.status = TaskStatus.SCHEDULED;
        this.currentRetries = 0;
    }

    public Task(String taskId, String taskName, String taskType, Map<String, Object> payload) {
        this();
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskType = taskType;
        this.payload = payload;
        this.priority = 5;
        this.maxRetries = 3;
    }

    // Getters and setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    
    public int getCurrentRetries() { return currentRetries; }
    public void setCurrentRetries(int currentRetries) { this.currentRetries = currentRetries; }
    
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public void incrementRetries() {
        this.currentRetries++;
    }

    public boolean canRetry() {
        return this.currentRetries < this.maxRetries;
    }
}
