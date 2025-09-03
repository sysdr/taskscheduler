package com.scheduler.dto;

import com.scheduler.entity.Task;

import java.time.LocalDateTime;

public record TaskDto(
    Long id,
    String name,
    String description,
    Task.TaskStatus status,
    LocalDateTime scheduledTime,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    String processorId,
    String errorMessage,
    Integer retryCount,
    Integer maxRetries,
    Long version,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TaskDto from(Task task) {
        return new TaskDto(
            task.getId(),
            task.getName(),
            task.getDescription(),
            task.getStatus(),
            task.getScheduledTime(),
            task.getStartedAt(),
            task.getCompletedAt(),
            task.getProcessorId(),
            task.getErrorMessage(),
            task.getRetryCount(),
            task.getMaxRetries(),
            task.getVersion(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
