package com.scheduler.backpressure.model;

import java.time.Instant;
import java.util.UUID;

public class Task {
    private String id;
    private String type;
    private String payload;
    private Instant createdAt;
    private int priority;
    
    public Task() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.priority = 5;
    }
    
    public Task(String type, String payload) {
        this();
        this.type = type;
        this.payload = payload;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
