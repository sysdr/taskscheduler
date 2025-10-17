package com.taskscheduler.exception;

public class TransientTaskException extends TaskExecutionException {
    public TransientTaskException(String message) {
        super(message);
    }
    
    public TransientTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
