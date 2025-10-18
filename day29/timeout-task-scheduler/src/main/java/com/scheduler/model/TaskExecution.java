package com.scheduler.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class TaskExecution {
    private final String taskId;
    private final String taskType;
    private final Duration timeout;
    private final String payload;
    private final LocalDateTime submittedAt;
    private final CompletableFuture<String> future;
    
    private TaskStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String result;
    private String error;
    private boolean warningIssued;

    public TaskExecution(TaskRequest request, CompletableFuture<String> future) {
        this.taskId = request.taskId();
        this.taskType = request.taskType();
        this.timeout = request.timeout();
        this.payload = request.payload();
        this.submittedAt = LocalDateTime.now();
        this.future = future;
        this.status = TaskStatus.SUBMITTED;
        this.warningIssued = false;
    }

    // Getters and setters
    public String getTaskId() { return taskId; }
    public String getTaskType() { return taskType; }
    public Duration getTimeout() { return timeout; }
    public String getPayload() { return payload; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public CompletableFuture<String> getFuture() { return future; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public boolean isWarningIssued() { return warningIssued; }
    public void setWarningIssued(boolean warningIssued) { this.warningIssued = warningIssued; }
    
    public Duration getElapsedTime() {
        if (startedAt == null) return Duration.ZERO;
        LocalDateTime endTime = completedAt != null ? completedAt : LocalDateTime.now();
        return Duration.between(startedAt, endTime);
    }
    
    public boolean isTimeoutApproaching(double warningThreshold) {
        if (startedAt == null) return false;
        Duration elapsed = getElapsedTime();
        return elapsed.compareTo(timeout.multipliedBy((long)(warningThreshold * 100)).dividedBy(100)) >= 0;
    }
}
