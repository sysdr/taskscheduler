package com.scheduler.model;

public enum TaskStatus {
    PENDING,
    QUEUED,
    RUNNING,
    LAMBDA_INVOKED,
    COMPLETED,
    FAILED,
    RETRYING
}
