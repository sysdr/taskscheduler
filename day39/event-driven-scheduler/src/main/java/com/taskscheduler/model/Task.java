package com.taskscheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskId;
    private String taskName;
    private String taskType;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;
    
    private String triggeredBy; // Event ID that triggered this task
    private String eventType;
    
    @Column(length = 2000)
    private String eventPayload;
    
    @Column(length = 2000)
    private String result;
    
    private Instant createdAt = Instant.now();
    private Instant startedAt;
    private Instant completedAt;
    
    private int executionTimeMs;
}
