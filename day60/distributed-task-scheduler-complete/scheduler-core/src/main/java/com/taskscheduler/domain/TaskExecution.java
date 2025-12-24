package com.taskscheduler.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_executions", indexes = {
    @Index(name = "idx_task_id", columnList = "taskId"),
    @Index(name = "idx_start_time", columnList = "startTime")
})
@Data
public class TaskExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exec_seq")
    @SequenceGenerator(name = "exec_seq", sequenceName = "execution_sequence", allocationSize = 50)
    private Long id;
    
    @Column(nullable = false)
    private Long taskId;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;
    
    @Column(length = 5000)
    private String output;
    
    @Column(length = 5000)
    private String errorMessage;
    
    private String executorInstance;
    private Long durationMs;
    
    public enum ExecutionStatus {
        STARTED, SUCCESS, FAILED, TIMEOUT, CANCELLED
    }
}
