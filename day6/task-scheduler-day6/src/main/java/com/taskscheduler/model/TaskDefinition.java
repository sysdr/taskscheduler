package com.taskscheduler.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Core entity representing a task definition in the distributed scheduler.
 * This is the blueprint that defines what, when, and how tasks should be executed.
 */
@Entity
@Table(name = "task_definitions", indexes = {
    @Index(name = "idx_task_status", columnList = "status"),
    @Index(name = "idx_task_type", columnList = "taskType"),
    @Index(name = "idx_next_run_time", columnList = "nextRunTime"),
    @Index(name = "idx_priority", columnList = "priority")
})
public class TaskDefinition {
    
    @Id
    @Column(length = 36)
    private String id;
    
    @NotBlank(message = "Task name is required")
    @Size(max = 100, message = "Task name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Task type is required")
    @Size(max = 50, message = "Task type must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String taskType;
    
    @NotBlank(message = "CRON expression is required")
    @Size(max = 100, message = "CRON expression must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String cronExpression;
    
    @Column(columnDefinition = "TEXT")
    private String payload;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.ACTIVE;
    
    @Min(value = 0, message = "Retry count cannot be negative")
    @Max(value = 10, message = "Retry count cannot exceed 10")
    @Column(nullable = false)
    private Integer retryCount = 0;
    
    @Min(value = 1, message = "Priority must be between 1 and 10")
    @Max(value = 10, message = "Priority must be between 1 and 10")
    @Column(nullable = false)
    private Integer priority = 5;
    
    @Min(value = 1, message = "Timeout must be at least 1 second")
    @Column(nullable = false)
    private Integer timeoutSeconds = 300; // 5 minutes default
    
    @Column(nullable = false)
    private LocalDateTime nextRunTime;
    
    @Size(max = 50, message = "Created by must not exceed 50 characters")
    @Column(length = 50)
    private String createdBy;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public TaskDefinition() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public TaskDefinition(String name, String taskType, String cronExpression) {
        this();
        this.name = name;
        this.taskType = taskType;
        this.cronExpression = cronExpression;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isEligibleToRun() {
        return status == TaskStatus.ACTIVE && 
               nextRunTime != null && 
               nextRunTime.isBefore(LocalDateTime.now());
    }
    
    public void updateNextRunTime(LocalDateTime nextRun) {
        this.nextRunTime = nextRun;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsFailed() {
        this.status = TaskStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void pause() {
        this.status = TaskStatus.PAUSED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void resume() {
        this.status = TaskStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Integer getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    
    public LocalDateTime getNextRunTime() { return nextRunTime; }
    public void setNextRunTime(LocalDateTime nextRunTime) { this.nextRunTime = nextRunTime; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDefinition that = (TaskDefinition) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "TaskDefinition{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", taskType='" + taskType + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", nextRunTime=" + nextRunTime +
                '}';
    }
}
