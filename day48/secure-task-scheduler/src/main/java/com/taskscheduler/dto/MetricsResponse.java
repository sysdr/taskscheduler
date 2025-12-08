package com.taskscheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetricsResponse {
    private long totalTasks;
    private long activeTasks;
    private long inactiveTasks;
    private long totalExecutions;
    private long successfulExecutions;
    private long failedExecutions;
    private long totalUsers;
    private long recentExecutions;
}

