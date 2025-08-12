package com.ultrascale.scheduler.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_executions")
public class TaskExecutionRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String taskName;
    
    @Column(nullable = false)
    private String instanceId;
    
    @Column(nullable = false)
    private LocalDateTime executionTime;
    
    @Column(nullable = false)
    private String status;
    
    private String details;
    
    // Constructors
    public TaskExecutionRecord() {}
    
    public TaskExecutionRecord(String taskName, String instanceId, LocalDateTime executionTime, String status, String details) {
        this.taskName = taskName;
        this.instanceId = instanceId;
        this.executionTime = executionTime;
        this.status = status;
        this.details = details;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    
    public LocalDateTime getExecutionTime() { return executionTime; }
    public void setExecutionTime(LocalDateTime executionTime) { this.executionTime = executionTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
