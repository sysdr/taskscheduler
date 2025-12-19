package com.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String type;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private ExecutionMode executionMode = ExecutionMode.LOCAL;
    
    private String functionName;
    
    @Column(length = 4096)
    private String payload;
    
    @Column(length = 4096)
    private String result;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    private Long executionTimeMs;
    private Double estimatedCost;
    
    private String lambdaRequestId;
    private Integer retryCount = 0;
    private Integer maxRetries = 3;
    
    @Column(length = 1024)
    private String errorMessage;
}
