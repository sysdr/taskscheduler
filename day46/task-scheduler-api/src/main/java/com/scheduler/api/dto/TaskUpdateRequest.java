package com.scheduler.api.dto;

import lombok.Data;

@Data
public class TaskUpdateRequest {
    private String description;
    private String cronExpression;
    private Boolean enabled;
    private Integer maxRetries;
    private Integer timeoutSeconds;
    private String payload;
}
