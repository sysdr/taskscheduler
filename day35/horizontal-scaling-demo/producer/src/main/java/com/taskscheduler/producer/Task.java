package com.taskscheduler.producer;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Task implements Serializable {
    private String id;
    private String type;
    private String payload;
    private int complexityMs;
    private LocalDateTime createdAt;
    
    public Task() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Task(String id, String type, String payload, int complexityMs) {
        this.id = id;
        this.type = type;
        this.payload = payload;
        this.complexityMs = complexityMs;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public int getComplexityMs() { return complexityMs; }
    public void setComplexityMs(int complexityMs) { this.complexityMs = complexityMs; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
