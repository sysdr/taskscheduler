package com.scheduler.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TaskCreateRequest {
    
    @NotBlank(message = "Task name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Cron expression is required")
    private String cronExpression;
    
    private Boolean enabled = true;
    
    @Min(value = 0, message = "Max retries must be non-negative")
    private Integer maxRetries = 3;
    
    @Min(value = 1, message = "Timeout must be at least 1 second")
    private Integer timeoutSeconds = 300;
    
    private String payload;
}
