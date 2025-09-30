package com.taskscheduler.exception;

public class TaskException extends Exception {
    private final boolean retriable;
    
    public TaskException(String message, boolean retriable) {
        super(message);
        this.retriable = retriable;
    }
    
    public TaskException(String message, Throwable cause, boolean retriable) {
        super(message, cause);
        this.retriable = retriable;
    }
    
    public boolean isRetriable() {
        return retriable;
    }
}
