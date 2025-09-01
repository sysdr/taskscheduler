package com.taskscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskExecutionRequest(
    @NotBlank(message = "Task key is required")
    @Size(max = 255, message = "Task key must not exceed 255 characters")
    String taskKey,
    
    @NotBlank(message = "Task type is required")
    @Size(max = 100, message = "Task type must not exceed 100 characters")  
    String taskType
) {}
