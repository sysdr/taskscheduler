package com.scheduler.model;

public enum TaskStatus {
    CREATED,
    PROCESSING,
    RETRYING,
    COMPLETED,
    DEAD_LETTER
}
