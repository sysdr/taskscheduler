package com.scheduler.model;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MetricsSnapshot {
    private int queueDepth, activeInstances, tasksProcessing;
}
