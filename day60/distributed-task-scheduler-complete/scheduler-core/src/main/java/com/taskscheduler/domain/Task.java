package com.taskscheduler.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_next_execution", columnList = "nextExecution")
})
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_sequence", allocationSize = 50)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, length = 2000)
    private String description;
    
    @Column(nullable = false)
    private String cronExpression;
    
    @Column(nullable = false, length = 1000)
    private String handlerClass;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.SCHEDULED;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;
    
    private LocalDateTime nextExecution;
    private LocalDateTime lastExecution;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    private Integer executionCount = 0;
    private Integer failureCount = 0;
    private Integer maxRetries = 3;
    
    @Column(length = 5000)
    private String lastError;
    
    private String createdBy;
    private String leaderInstance;
    
    @Column(length = 2000)
    private String taskParameters;
    
    private Boolean enabled = true;
    private String timezone = "UTC";
    
    public enum TaskStatus {
        SCHEDULED, RUNNING, COMPLETED, FAILED, PAUSED, CANCELLED
    }
    
    public enum TaskPriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
