package com.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type;
    
    private String cronExpression;
    
    private Long fixedDelayMs;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_execution")
    private LocalDateTime lastExecution;
    
    @Column(name = "next_execution")
    private LocalDateTime nextExecution;
    
    private Integer executionCount;
    
    private Integer failureCount;
    
    private Long avgExecutionTimeMs;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        executionCount = 0;
        failureCount = 0;
        status = TaskStatus.SCHEDULED;
    }
}
