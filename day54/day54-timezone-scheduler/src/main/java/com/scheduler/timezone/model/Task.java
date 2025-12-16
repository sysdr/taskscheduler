package com.scheduler.timezone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;
    
    @Column(name = "time_zone", nullable = false)
    private String timeZone;
    
    @Column(name = "next_run_utc", nullable = false)
    private Instant nextRunUtc;
    
    @Column(name = "original_scheduled_time", length = 500)
    private String originalScheduledTime;
    
    @Column(name = "last_execution_utc")
    private Instant lastExecutionUtc;
    
    @Column(name = "execution_count")
    @Builder.Default
    private Long executionCount = 0L;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskStatus status = TaskStatus.ACTIVE;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
