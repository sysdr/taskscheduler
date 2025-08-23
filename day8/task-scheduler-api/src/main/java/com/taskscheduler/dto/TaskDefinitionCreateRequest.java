package com.taskscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.taskscheduler.entity.TaskDefinition;

public record TaskDefinitionCreateRequest(
    @NotBlank(message = "Task name is required")
    @Size(max = 255, message = "Task name must not exceed 255 characters")
    String name,
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,
    
    @NotBlank(message = "Cron expression is required")
    @Size(max = 100, message = "Cron expression must not exceed 100 characters")
    String cronExpression,
    
    @NotNull(message = "Task status is required")
    TaskDefinition.TaskStatus status,
    
    @Size(max = 500, message = "Task class must not exceed 500 characters")
    String taskClass,
    
    @Size(max = 2000, message = "Parameters must not exceed 2000 characters")
    String parameters
) {
    public TaskDefinition toEntity() {
        TaskDefinition task = new TaskDefinition();
        task.setName(name);
        task.setDescription(description);
        task.setCronExpression(cronExpression);
        task.setStatus(status);
        task.setTaskClass(taskClass);
        task.setParameters(parameters);
        return task;
    }
}
