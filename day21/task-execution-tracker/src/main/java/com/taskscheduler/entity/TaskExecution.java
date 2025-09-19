package com.taskscheduler.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_executions", indexes = {
    @Index(name = "idx_execution_id", columnList = "executionId"),
    @Index(name = "idx_task_name", columnList = "taskName"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_start_time", columnList = "startTime")
})
public class TaskExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String executionId;
    
    @NotNull
    @Size(min = 1, max = 100)
    private String taskName;
    
    @NotNull
    @Size(min = 1, max = 200)
    private String taskDescription;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Long durationMs;
    
    @Column(length = 500)
    private String errorMessage;
    
    @Column(length = 2000)
    private String stackTrace;
    
    private String nodeId;
    
    private Integer retryCount = 0;
    
    @Column(length = 1000)
    private String metadata;
    
    // Constructors
    public TaskExecution() {
        this.executionId = UUID.randomUUID().toString();
        this.status = ExecutionStatus.PENDING;
        this.startTime = LocalDateTime.now();
    }
    
    public TaskExecution(String taskName, String taskDescription) {
        this();
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    
    public ExecutionStatus getStatus() { return status; }
    public void setStatus(ExecutionStatus status) { this.status = status; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
    
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    // Helper methods
    public void markAsRunning(String nodeId) {
        this.status = ExecutionStatus.RUNNING;
        this.nodeId = nodeId;
        this.startTime = LocalDateTime.now();
    }
    
    public void markAsCompleted() {
        this.status = ExecutionStatus.SUCCESS;
        this.endTime = LocalDateTime.now();
        this.durationMs = java.time.Duration.between(startTime, endTime).toMillis();
    }
    
    public void markAsFailed(String errorMessage, String stackTrace) {
        this.status = ExecutionStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
    }
}
