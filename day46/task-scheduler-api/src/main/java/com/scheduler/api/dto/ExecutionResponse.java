package com.scheduler.api.dto;

import com.scheduler.api.model.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResponse {
    private Long id;
    private Long taskId;
    private String taskName;
    private ExecutionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
    private String result;
    private String errorMessage;
    private Integer attemptNumber;
}
