package com.taskscheduler.dto;

import com.taskscheduler.entity.TaskExecution;

import java.time.LocalDateTime;

public record TaskExecutionResponse(
    Long id,
    String taskKey,
    String instanceId,
    String status,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    Long durationMs,
    String result,
    String errorMessage
) {
    public static TaskExecutionResponse from(TaskExecution execution) {
        return new TaskExecutionResponse(
            execution.getId(),
            execution.getTaskKey(),
            execution.getInstanceId(),
            execution.getStatus().name(),
            execution.getStartedAt(),
            execution.getCompletedAt(),
            execution.getDurationMs(),
            execution.getResult(),
            execution.getErrorMessage()
        );
    }
}
