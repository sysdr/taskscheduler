package com.taskscheduler.batch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String batchId;
    
    private Integer batchSize;
    private Integer successCount;
    private Integer failureCount;
    private Long totalProcessingTimeMs;
    private Double avgTaskProcessingTimeMs;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Column(length = 500)
    private String processorInfo;
}
