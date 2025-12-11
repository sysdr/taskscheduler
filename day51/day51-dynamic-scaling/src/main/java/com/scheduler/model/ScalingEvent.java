package com.scheduler.model;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ScalingEvent {
    private String id;
    private ScalingAction action;
    private int before, after;
    private String reason;
    private double metric;
    private LocalDateTime timestamp;
}
