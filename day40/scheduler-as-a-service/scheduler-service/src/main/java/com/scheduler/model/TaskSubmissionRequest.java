package com.scheduler.model;

import java.time.LocalDateTime;

public class TaskSubmissionRequest {
    private String taskName;
    private String payload;
    private String callbackUrl;
    private LocalDateTime scheduledFor;
    private Integer priority;
    private Integer maxRetries;
    
    // Getters and Setters
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    
    public LocalDateTime getScheduledFor() { return scheduledFor; }
    public void setScheduledFor(LocalDateTime scheduledFor) { this.scheduledFor = scheduledFor; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
}
