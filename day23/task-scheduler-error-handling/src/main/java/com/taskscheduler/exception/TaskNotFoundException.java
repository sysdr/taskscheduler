package com.taskscheduler.exception;

public class TaskNotFoundException extends RuntimeException {
    
    public TaskNotFoundException(String message) {
        super(message);
    }
}
