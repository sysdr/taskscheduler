package com.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String type;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long durationMs;
    private String errorMessage;
    
    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, FAILED
    }
}
