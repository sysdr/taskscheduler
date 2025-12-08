package com.taskscheduler.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_executions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;
    
    @Column(length = 2000)
    private String result;
    
    @Column(length = 2000)
    private String errorMessage;
    
    private Long durationMs;
    
    public enum ExecutionStatus {
        SUCCESS, FAILED, RUNNING, TIMEOUT
    }
}
