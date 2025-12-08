package com.taskscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskRequest {
    @NotBlank
    private String name;
    
    private String description;
    
    @NotBlank
    private String cronExpression;
}
