package com.taskscheduler.model;

public class TaskExecutionResult {
    private final boolean success;
    private final String message;
    private final Exception exception;
    private final long executionTimeMs;
    
    private TaskExecutionResult(boolean success, String message, Exception exception, long executionTimeMs) {
        this.success = success;
        this.message = message;
        this.exception = exception;
        this.executionTimeMs = executionTimeMs;
    }
    
    public static TaskExecutionResult success(String message, long executionTimeMs) {
        return new TaskExecutionResult(true, message, null, executionTimeMs);
    }
    
    public static TaskExecutionResult failure(String message, Exception exception, long executionTimeMs) {
        return new TaskExecutionResult(false, message, exception, executionTimeMs);
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Exception getException() { return exception; }
    public long getExecutionTimeMs() { return executionTimeMs; }
}
