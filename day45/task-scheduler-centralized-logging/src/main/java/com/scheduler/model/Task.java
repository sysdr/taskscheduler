package com.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String taskId;
    private String userId;
    private String taskType;
    private String status;
    private LocalDateTime scheduledTime;
    private LocalDateTime executionTime;
    private Integer executionDurationMs;
    private String errorMessage;
    private Integer retryCount;
}
