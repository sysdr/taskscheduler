package com.scheduler.dto;

import java.time.LocalDateTime;

public class TaskCreateRequest {
    private String name;
    private String payload;
    private LocalDateTime scheduledAt;
    private Integer maxRetries;
    
    // Constructors
    public TaskCreateRequest() {}
    
    public TaskCreateRequest(String name, String payload) {
        this.name = name;
        this.payload = payload;
        this.scheduledAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
}
