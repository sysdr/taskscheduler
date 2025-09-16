package com.taskscheduler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_tasks")
public class ScheduledTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String taskId;
    
    @Column(nullable = false)
    private String taskType;
    
    @Column(nullable = false)
    private String status;
    
    @Column
    private String payload;
    
    @Column
    private LocalDateTime scheduledTime;
    
    @Column
    private LocalDateTime executedTime;
    
    @Column
    private String executorInstance;
    
    @Column
    private Integer retryCount = 0;
    
    @Column
    private String errorMessage;

    // Default constructor
    public ScheduledTask() {}

    // Constructor with required fields
    public ScheduledTask(String taskId, String taskType, String status, String payload, LocalDateTime scheduledTime) {
        this.taskId = taskId;
        this.taskType = taskType;
        this.status = status;
        this.payload = payload;
        this.scheduledTime = scheduledTime;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    
    public LocalDateTime getExecutedTime() { return executedTime; }
    public void setExecutedTime(LocalDateTime executedTime) { this.executedTime = executedTime; }
    
    public String getExecutorInstance() { return executorInstance; }
    public void setExecutorInstance(String executorInstance) { this.executorInstance = executorInstance; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
