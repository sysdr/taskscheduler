package com.scheduler.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskMetrics {
    private Long totalTasks;
    private Long runningTasks;
    private Long failedTasks;
    private Long totalExecutions;
    private Long avgExecutionTimeMs;
    private Double successRate;
}
