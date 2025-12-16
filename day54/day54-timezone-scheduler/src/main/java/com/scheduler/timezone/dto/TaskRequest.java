package com.scheduler.timezone.dto;

import lombok.Data;

@Data
public class TaskRequest {
    private String name;
    private String description;
    private String cronExpression;
    private String timeZone;
    private String scheduledTime; // Format: "HH:mm"
}
