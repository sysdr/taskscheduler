package com.scheduler.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private String id;
    private String name;
    private String payload;
    private TaskPriority priority;
    private LocalDateTime submittedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private TaskStatus status;
    
    public Task() {
        this.id = UUID.randomUUID().toString();
        this.submittedAt = LocalDateTime.now();
        this.status = TaskStatus.PENDING;
    }
    
    public Task(String name, String payload, TaskPriority priority) {
        this();
        this.name = name;
        this.payload = payload;
        this.priority = priority;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
}
