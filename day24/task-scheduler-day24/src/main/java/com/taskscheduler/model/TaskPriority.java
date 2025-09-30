package com.taskscheduler.model;

public enum TaskPriority {
    LOW(1),
    NORMAL(2),
    HIGH(3),
    CRITICAL(4);
    
    private final int value;
    
    TaskPriority(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
