package com.taskscheduler.model;

import java.time.LocalDateTime;

public class TaskResult {
    private boolean success;
    private String message;
    private LocalDateTime completedAt;
    private int retryAttempt;
    private Exception exception;
    
    private TaskResult(boolean success, String message, int retryAttempt, Exception exception) {
        this.success = success;
        this.message = message;
        this.retryAttempt = retryAttempt;
        this.exception = exception;
        this.completedAt = LocalDateTime.now();
    }
    
    public static TaskResult success(String message) {
        return new TaskResult(true, message, 0, null);
    }
    
    public static TaskResult success(String message, int retryAttempt) {
        return new TaskResult(true, message, retryAttempt, null);
    }
    
    public static TaskResult failure(String message, Exception exception) {
        return new TaskResult(false, message, 0, exception);
    }
    
    public static TaskResult failure(String message, int retryAttempt, Exception exception) {
        return new TaskResult(false, message, retryAttempt, exception);
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public int getRetryAttempt() { return retryAttempt; }
    public Exception getException() { return exception; }
}
