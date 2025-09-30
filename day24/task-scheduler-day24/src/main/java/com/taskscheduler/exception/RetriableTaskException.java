package com.taskscheduler.exception;

public class RetriableTaskException extends TaskException {
    public RetriableTaskException(String message) {
        super(message, true);
    }
    
    public RetriableTaskException(String message, Throwable cause) {
        super(message, cause, true);
    }
}
