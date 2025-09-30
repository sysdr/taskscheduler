package com.taskscheduler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "task_type", nullable = false)
    private String taskType;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "started_time")
    private LocalDateTime startedTime;
    
    @Column(name = "completed_time")
    private LocalDateTime completedTime;
    
    @Column(name = "last_attempt_time")
    private LocalDateTime lastAttemptTime;
    
    @Column(name = "next_retry_time")
    private LocalDateTime nextRetryTime;
    
    @Column(name = "attempt_count")
    private Integer attemptCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "task_data", columnDefinition = "TEXT")
    private String taskData;
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    
    // Constructors
    public Task() {
        this.createdTime = LocalDateTime.now();
        this.status = TaskStatus.PENDING;
        this.priority = TaskPriority.NORMAL;
    }
    
    public Task(String name, String taskType) {
        this();
        this.name = name;
        this.taskType = taskType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public LocalDateTime getStartedTime() { return startedTime; }
    public void setStartedTime(LocalDateTime startedTime) { this.startedTime = startedTime; }
    
    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
    
    public LocalDateTime getLastAttemptTime() { return lastAttemptTime; }
    public void setLastAttemptTime(LocalDateTime lastAttemptTime) { this.lastAttemptTime = lastAttemptTime; }
    
    public LocalDateTime getNextRetryTime() { return nextRetryTime; }
    public void setNextRetryTime(LocalDateTime nextRetryTime) { this.nextRetryTime = nextRetryTime; }
    
    public Integer getAttemptCount() { return attemptCount; }
    public void setAttemptCount(Integer attemptCount) { this.attemptCount = attemptCount; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getTaskData() { return taskData; }
    public void setTaskData(String taskData) { this.taskData = taskData; }
    
    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    
    // Utility methods
    public boolean canRetry() {
        return attemptCount < maxRetries && status == TaskStatus.FAILED;
    }
    
    public void incrementAttemptCount() {
        this.attemptCount++;
        this.lastAttemptTime = LocalDateTime.now();
    }
    
    public boolean isRetriable() {
        return canRetry() && nextRetryTime != null && 
               LocalDateTime.now().isAfter(nextRetryTime);
    }
    
    @Override
    public String toString() {
        return String.format("Task{id=%d, name='%s', status=%s, attempts=%d/%d}", 
                           id, name, status, attemptCount, maxRetries);
    }
}
