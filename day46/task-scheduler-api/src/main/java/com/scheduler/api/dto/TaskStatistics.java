package com.scheduler.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatistics {
    private Long taskId;
    private String taskName;
    private Long totalExecutions;
    private Long successfulExecutions;
    private Long failedExecutions;
    private Double successRate;
    private Long averageDurationMs;
    private List<ExecutionResponse> recentExecutions;
}
