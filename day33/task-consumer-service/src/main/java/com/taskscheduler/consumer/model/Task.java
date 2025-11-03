package com.taskscheduler.consumer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String taskId;
    
    @Column(nullable = false)
    private String taskType;
    
    @Lob
    private String payload;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.QUEUED;
    
    private String workerId;
    private LocalDateTime startTime;
    private LocalDateTime completedTime;
    private String errorMessage;
    private int retryCount = 0;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public Task() {}
    
    public Task(String taskId, String taskType, String payload) {
        this.taskId = taskId;
        this.taskType = taskType;
        this.payload = payload;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public enum TaskStatus {
        QUEUED, PROCESSING, COMPLETED, FAILED, RETRY
    }
}
