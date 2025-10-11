package com.scheduler.exception;

public class TaskProcessingException extends Exception {
    private final String taskId;
    private final boolean retryable;
    
    public TaskProcessingException(String message, String taskId) {
        this(message, taskId, true, null);
    }
    
    public TaskProcessingException(String message, String taskId, boolean retryable) {
        this(message, taskId, retryable, null);
    }
    
    public TaskProcessingException(String message, String taskId, boolean retryable, Throwable cause) {
        super(message, cause);
        this.taskId = taskId;
        this.retryable = retryable;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public boolean isRetryable() {
        return retryable;
    }
}
