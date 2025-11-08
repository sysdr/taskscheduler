package com.scheduler.model;

public enum TaskPriority {
    LOW(1),
    NORMAL(5),
    HIGH(10);
    
    private final int value;
    
    TaskPriority(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getQueueName() {
        return "task.queue." + this.name().toLowerCase();
    }
}
