package com.scheduler.timezone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "task_executions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "task_id", nullable = false)
    private String taskId;
    
    @Column(name = "execution_time_utc", nullable = false)
    private Instant executionTimeUtc;
    
    @Column(name = "execution_time_local", nullable = false)
    private String executionTimeLocal;
    
    @Column(name = "time_zone", nullable = false)
    private String timeZone;
    
    @Column(name = "dst_in_effect")
    private Boolean dstInEffect;
    
    @Column(name = "utc_offset")
    private String utcOffset;
    
    @Column(name = "execution_status")
    private String executionStatus;
    
    @Column(name = "duration_ms")
    private Long durationMs;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
