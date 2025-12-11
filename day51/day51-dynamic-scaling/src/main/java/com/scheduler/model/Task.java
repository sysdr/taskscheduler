package com.scheduler.model;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Task {
    private String id;
    private String type;
    private TaskStatus status;
    private LocalDateTime createdAt, startedAt, completedAt;
    private String instanceId;
}
