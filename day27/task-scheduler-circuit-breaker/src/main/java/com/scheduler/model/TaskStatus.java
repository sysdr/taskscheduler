package com.scheduler.model;

public enum TaskStatus {
    PENDING,
    EXECUTING,
    SUCCESS,
    FAILED,
    CIRCUIT_BREAKER_OPEN
}
