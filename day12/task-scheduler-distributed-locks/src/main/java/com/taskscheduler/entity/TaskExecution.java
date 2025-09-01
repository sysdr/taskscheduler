package com.taskscheduler.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "task_executions")
public class TaskExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_exec_seq")
    @SequenceGenerator(name = "task_exec_seq", sequenceName = "task_exec_seq", allocationSize = 1)
    private Long id;
    
    @Column(name = "task_key", nullable = false, length = 255)
    private String taskKey;
    
    @Column(name = "instance_id", nullable = false, length = 255)
    private String instanceId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExecutionStatus status;
    
    @CreationTimestamp
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "duration_ms")
    private Long durationMs;
    
    @Column(name = "result", length = 1000)
    private String result;
    
    @Column(name = "error_message", length = 2000)
    private String errorMessage;
    
    @UpdateTimestamp
    private LocalDateTime lastModified;
    
    public enum ExecutionStatus {
        RUNNING, COMPLETED, FAILED, TIMEOUT
    }
    
    // Constructors
    public TaskExecution() {}
    
    public TaskExecution(String taskKey, String instanceId) {
        this.taskKey = taskKey;
        this.instanceId = instanceId;
        this.status = ExecutionStatus.RUNNING;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTaskKey() { return taskKey; }
    public void setTaskKey(String taskKey) { this.taskKey = taskKey; }
    
    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    
    public ExecutionStatus getStatus() { return status; }
    public void setStatus(ExecutionStatus status) { this.status = status; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    // Business methods
    public void markCompleted(String result) {
        this.status = ExecutionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.result = result;
        if (startedAt != null) {
            this.durationMs = java.time.Duration.between(startedAt, completedAt).toMillis();
        }
    }
    
    public void markFailed(String errorMessage) {
        this.status = ExecutionStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
        if (startedAt != null) {
            this.durationMs = java.time.Duration.between(startedAt, completedAt).toMillis();
        }
    }
    
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
}
