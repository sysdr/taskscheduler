package com.taskscheduler.entity;

import com.taskscheduler.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a task execution with status tracking
 */
@Entity
@Table(name = "task_executions", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_task_name", columnList = "taskName"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class TaskExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "task_name", nullable = false)
    private String taskName;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.PENDING;
    
    @Column(name = "execution_details")
    private String executionDetails;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "duration_ms")
    private Long durationMs;
    
    // Constructors
    public TaskExecution() {}
    
    public TaskExecution(String taskName) {
        this.taskName = taskName;
        this.status = TaskStatus.PENDING;
    }
    
    public TaskExecution(String taskName, String executionDetails) {
        this.taskName = taskName;
        this.executionDetails = executionDetails;
        this.status = TaskStatus.PENDING;
    }
    
    // Business methods
    public void start() {
        if (!this.status.canTransitionTo(TaskStatus.RUNNING)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to RUNNING", this.status)
            );
        }
        this.status = TaskStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }
    
    public void succeed() {
        if (!this.status.canTransitionTo(TaskStatus.SUCCEEDED)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to SUCCEEDED", this.status)
            );
        }
        this.status = TaskStatus.SUCCEEDED;
        this.completedAt = LocalDateTime.now();
        calculateDuration();
    }
    
    public void fail(String errorMessage) {
        if (!this.status.canTransitionTo(TaskStatus.FAILED)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to FAILED", this.status)
            );
        }
        this.status = TaskStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
        calculateDuration();
    }
    
    private void calculateDuration() {
        if (startedAt != null && completedAt != null) {
            this.durationMs = java.time.Duration.between(startedAt, completedAt).toMillis();
        }
    }
    
    public void incrementRetryCount() {
        this.retryCount = (this.retryCount == null) ? 1 : this.retryCount + 1;
    }
    
    public boolean isCompleted() {
        return status.isCompleted();
    }
    
    public boolean isActive() {
        return status.isActive();
    }
    
    public boolean isTerminal() {
        return status.isTerminal();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public String getExecutionDetails() { return executionDetails; }
    public void setExecutionDetails(String executionDetails) { this.executionDetails = executionDetails; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Long getDurationMs() { return durationMs; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskExecution that = (TaskExecution) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("TaskExecution{id=%d, taskName='%s', status=%s, createdAt=%s}", 
                           id, taskName, status, createdAt);
    }
}
