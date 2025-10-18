package com.scheduler.model;

import java.time.Duration;

public record TaskRequest(
    String taskId,
    String taskType,
    Duration timeout,
    String payload
) {
    public TaskRequest {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        }
        if (timeout == null || timeout.isNegative()) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
    }
}
