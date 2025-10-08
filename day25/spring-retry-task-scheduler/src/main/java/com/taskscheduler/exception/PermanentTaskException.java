package com.taskscheduler.exception;

public class PermanentTaskException extends TaskExecutionException {
    public PermanentTaskException(String message) {
        super(message);
    }
    
    public PermanentTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
