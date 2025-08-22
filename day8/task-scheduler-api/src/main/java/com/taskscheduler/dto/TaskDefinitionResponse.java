package com.taskscheduler.dto;

import com.taskscheduler.entity.TaskDefinition;
import java.time.LocalDateTime;

public record TaskDefinitionResponse(
    Long id,
    String name,
    String description,
    String cronExpression,
    TaskDefinition.TaskStatus status,
    String taskClass,
    String parameters,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TaskDefinitionResponse fromEntity(TaskDefinition task) {
        return new TaskDefinitionResponse(
            task.getId(),
            task.getName(),
            task.getDescription(),
            task.getCronExpression(),
            task.getStatus(),
            task.getTaskClass(),
            task.getParameters(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
