package com.taskscheduler.batch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_batch_id", columnList = "batchId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String taskId;
    
    @Column(nullable = false)
    private String taskType;
    
    @Column(length = 1000)
    private String payload;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    
    private String batchId;
    
    private Integer retryCount;
    
    @Column(length = 2000)
    private String errorMessage;
    
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private Long processingDurationMs;
    
    @Version
    private Long version;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        retryCount = 0;
    }
    
    public enum TaskStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        RETRY
    }
}
