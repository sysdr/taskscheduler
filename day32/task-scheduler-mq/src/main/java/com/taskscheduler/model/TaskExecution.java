package com.taskscheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class TaskExecution {
    private String executionId;
    private String taskId;
    private String workerId;
    private TaskStatus status;
    private String result;
    private String errorMessage;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private long executionTimeMs;

    public TaskExecution() {}

    public TaskExecution(String executionId, String taskId, String workerId) {
        this.executionId = executionId;
        this.taskId = taskId;
        this.workerId = workerId;
        this.startTime = LocalDateTime.now();
        this.status = TaskStatus.PROCESSING;
    }

    // Getters and setters
    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public void markCompleted(String result) {
        this.endTime = LocalDateTime.now();
        this.result = result;
        this.status = TaskStatus.COMPLETED;
        this.executionTimeMs = java.time.Duration.between(startTime, endTime).toMillis();
    }

    public void markFailed(String errorMessage) {
        this.endTime = LocalDateTime.now();
        this.errorMessage = errorMessage;
        this.status = TaskStatus.FAILED;
        this.executionTimeMs = java.time.Duration.between(startTime, endTime).toMillis();
    }
}
