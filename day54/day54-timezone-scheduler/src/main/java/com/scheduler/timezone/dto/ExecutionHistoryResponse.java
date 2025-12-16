package com.scheduler.timezone.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ExecutionHistoryResponse {
    private String id;
    private String taskId;
    private String taskName;
    private Instant executionTimeUtc;
    private String executionTimeLocal;
    private String timeZone;
    private Boolean dstInEffect;
    private String utcOffset;
    private String executionStatus;
    private Long durationMs;
    private String notes;
}
