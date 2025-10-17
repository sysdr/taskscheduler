package com.taskscheduler.exception;

public class EmailServiceException extends TransientTaskException {
    public EmailServiceException(String message) {
        super(message);
    }
    
    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
