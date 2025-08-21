package com.taskscheduler.exception;

public class TaskNameAlreadyExistsException extends RuntimeException {
    public TaskNameAlreadyExistsException(String name) {
        super("Task with name '" + name + "' already exists");
    }
}
