package com.scheduler.exception;

public class TaskLockException extends RuntimeException {
    private final Long taskId;
    private final String processorId;
    
    public TaskLockException(Long taskId, String processorId, String message) {
        super(message);
        this.taskId = taskId;
        this.processorId = processorId;
    }
    
    public TaskLockException(Long taskId, String processorId, String message, Throwable cause) {
        super(message, cause);
        this.taskId = taskId;
        this.processorId = processorId;
    }
    
    public Long getTaskId() { return taskId; }
    public String getProcessorId() { return processorId; }
}
