package com.scheduler.api.model;

import jakarta.persistence.*;
import lombok.*;
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
    
    @Column(nullable = false)
    private Long taskId;
    
    @Column(nullable = false)
    private String taskName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Long durationMs;
    
    @Column(columnDefinition = "TEXT")
    private String result;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    private Integer attemptNumber = 1;
    
    @PrePersist
    protected void onCreate() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }
}
