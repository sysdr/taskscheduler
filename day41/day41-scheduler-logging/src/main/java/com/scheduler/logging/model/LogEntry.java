package com.scheduler.logging.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "log_entries")
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Instant timestamp = Instant.now();
    private String level;
    private String taskId;
    private String taskType;
    private String correlationId;
    private String message;
    private Long durationMs;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
}
