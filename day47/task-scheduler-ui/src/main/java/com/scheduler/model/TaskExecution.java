package com.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_executions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_id", nullable = false)
    private Long taskId;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    
    @Column(length = 2000)
    private String errorMessage;
    
    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }
}
