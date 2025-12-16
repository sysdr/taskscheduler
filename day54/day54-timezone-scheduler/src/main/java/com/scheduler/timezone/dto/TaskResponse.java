package com.scheduler.timezone.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class TaskResponse {
    private String id;
    private String name;
    private String description;
    private String cronExpression;
    private String timeZone;
    private Instant nextRunUtc;
    private String nextRunLocal;
    private Instant lastExecutionUtc;
    private Long executionCount;
    private String status;
    private Boolean dstActive;
    private String utcOffset;
    private String dstWarning;
}
