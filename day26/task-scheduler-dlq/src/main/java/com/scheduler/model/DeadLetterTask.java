package com.scheduler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dead_letter_tasks")
public class DeadLetterTask {
    @Id
    private String id = UUID.randomUUID().toString();
    
    @Column(nullable = false)
    private String originalTaskId;
    
    @Column(nullable = false)
    private String taskName;
    
    @Column(columnDefinition = "TEXT")
    private String taskPayload;
    
    private int totalRetryAttempts;
    
    @Enumerated(EnumType.STRING)
    private FailureReason failureReason;
    
    @Column(columnDefinition = "TEXT")
    private String finalException;
    
    @Column(columnDefinition = "TEXT")
    private String stackTrace;
    
    @Column(columnDefinition = "TEXT")
    private String systemContext;
    
    private LocalDateTime deadLetteredAt = LocalDateTime.now();
    private LocalDateTime originalCreatedAt;
    
    private boolean reprocessed = false;
    private LocalDateTime reprocessedAt;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // Constructors
    public DeadLetterTask() {}
    
    public DeadLetterTask(Task originalTask, String finalException, String stackTrace) {
        this.originalTaskId = originalTask.getId();
        this.taskName = originalTask.getName();
        this.taskPayload = originalTask.getPayload();
        this.totalRetryAttempts = originalTask.getRetryCount();
        this.finalException = finalException;
        this.stackTrace = stackTrace;
        this.originalCreatedAt = originalTask.getCreatedAt();
        this.failureReason = determineFailureReason(finalException);
        this.systemContext = buildSystemContext();
    }
    
    private FailureReason determineFailureReason(String exception) {
        if (exception == null) return FailureReason.UNKNOWN;
        if (exception.contains("timeout") || exception.contains("TimeoutException")) {
            return FailureReason.TIMEOUT;
        }
        if (exception.contains("Connection") || exception.contains("network")) {
            return FailureReason.NETWORK_ERROR;
        }
        if (exception.contains("validation") || exception.contains("ValidationException")) {
            return FailureReason.VALIDATION_ERROR;
        }
        if (exception.contains("IllegalArgument") || exception.contains("InvalidData")) {
            return FailureReason.INVALID_DATA;
        }
        return FailureReason.PROCESSING_ERROR;
    }
    
    private String buildSystemContext() {
        Runtime runtime = Runtime.getRuntime();
        return String.format(
            "Memory: %d MB free, %d MB total, %d MB max | Processors: %d | Timestamp: %s",
            runtime.freeMemory() / 1024 / 1024,
            runtime.totalMemory() / 1024 / 1024,
            runtime.maxMemory() / 1024 / 1024,
            runtime.availableProcessors(),
            LocalDateTime.now()
        );
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getOriginalTaskId() { return originalTaskId; }
    public void setOriginalTaskId(String originalTaskId) { this.originalTaskId = originalTaskId; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getTaskPayload() { return taskPayload; }
    public void setTaskPayload(String taskPayload) { this.taskPayload = taskPayload; }
    
    public int getTotalRetryAttempts() { return totalRetryAttempts; }
    public void setTotalRetryAttempts(int totalRetryAttempts) { this.totalRetryAttempts = totalRetryAttempts; }
    
    public FailureReason getFailureReason() { return failureReason; }
    public void setFailureReason(FailureReason failureReason) { this.failureReason = failureReason; }
    
    public String getFinalException() { return finalException; }
    public void setFinalException(String finalException) { this.finalException = finalException; }
    
    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
    
    public String getSystemContext() { return systemContext; }
    public void setSystemContext(String systemContext) { this.systemContext = systemContext; }
    
    public LocalDateTime getDeadLetteredAt() { return deadLetteredAt; }
    public void setDeadLetteredAt(LocalDateTime deadLetteredAt) { this.deadLetteredAt = deadLetteredAt; }
    
    public LocalDateTime getOriginalCreatedAt() { return originalCreatedAt; }
    public void setOriginalCreatedAt(LocalDateTime originalCreatedAt) { this.originalCreatedAt = originalCreatedAt; }
    
    public boolean isReprocessed() { return reprocessed; }
    public void setReprocessed(boolean reprocessed) { this.reprocessed = reprocessed; }
    
    public LocalDateTime getReprocessedAt() { return reprocessedAt; }
    public void setReprocessedAt(LocalDateTime reprocessedAt) { this.reprocessedAt = reprocessedAt; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public void markAsReprocessed() {
        this.reprocessed = true;
        this.reprocessedAt = LocalDateTime.now();
    }
}
