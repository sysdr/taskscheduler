package com.scheduler.api.dto;

import com.scheduler.api.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private String cronExpression;
    private Boolean enabled;
    private TaskStatus status;
    private Integer maxRetries;
    private Integer timeoutSeconds;
    private String payload;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastExecutedAt;
}
