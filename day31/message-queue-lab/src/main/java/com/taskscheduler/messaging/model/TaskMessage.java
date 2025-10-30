package com.taskscheduler.messaging.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public class TaskMessage {
    
    @JsonProperty("taskId")
    private String taskId;
    
    @JsonProperty("taskType")
    private String taskType;
    
    @JsonProperty("payload")
    private String payload;
    
    @JsonProperty("priority")
    private int priority;
    
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @JsonProperty("retryCount")
    private int retryCount;

    public TaskMessage() {
        this.taskId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.retryCount = 0;
    }

    public TaskMessage(String taskType, String payload, int priority) {
        this();
        this.taskType = taskType;
        this.payload = payload;
        this.priority = priority;
    }

    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    @Override
    public String toString() {
        return String.format("TaskMessage{taskId='%s', taskType='%s', priority=%d, retryCount=%d}", 
                           taskId, taskType, priority, retryCount);
    }
}
