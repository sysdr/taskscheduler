package com.taskscheduler.exception;

public class NonRetriableTaskException extends TaskException {
    public NonRetriableTaskException(String message) {
        super(message, false);
    }
    
    public NonRetriableTaskException(String message, Throwable cause) {
        super(message, cause, false);
    }
}
