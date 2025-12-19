package com.scheduler.model;

public enum ExecutionMode {
    LOCAL,    // Execute in local worker pool
    LAMBDA,   // Execute in AWS Lambda
    AUTO      // Scheduler decides based on load
}
