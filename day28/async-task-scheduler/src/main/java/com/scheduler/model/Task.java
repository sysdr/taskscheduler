package com.scheduler.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.concurrent.Future;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String type;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String result;
    private String errorMessage;
    private Integer processingTimeSeconds;
    
    @Transient
    private Future<String> futureResult;
    
    public Task() {
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public Task(String name, String type, String description, Integer processingTimeSeconds) {
        this();
        this.name = name;
        this.type = type;
        this.description = description;
        this.processingTimeSeconds = processingTimeSeconds;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Integer getProcessingTimeSeconds() { return processingTimeSeconds; }
    public void setProcessingTimeSeconds(Integer processingTimeSeconds) { this.processingTimeSeconds = processingTimeSeconds; }
    
    public Future<String> getFutureResult() { return futureResult; }
    public void setFutureResult(Future<String> futureResult) { this.futureResult = futureResult; }
}
