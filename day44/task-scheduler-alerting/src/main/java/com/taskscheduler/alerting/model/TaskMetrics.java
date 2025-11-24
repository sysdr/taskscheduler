package com.taskscheduler.alerting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskMetrics {
    private long totalTasks;
    private long completedTasks;
    private long failedTasks;
    private long runningTasks;
    private double failureRate;
    private double avgDurationMs;
    private double p95DurationMs;
    private long queueDepth;
    private long timestamp;
}
