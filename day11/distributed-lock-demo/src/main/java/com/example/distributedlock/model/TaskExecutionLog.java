package com.example.distributedlock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_execution_log")
public class TaskExecutionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_name")
    private String taskName;
    
    @Column(name = "instance_id")
    private String instanceId;
    
    @Column(name = "execution_time")
    private LocalDateTime executionTime;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "details")
    private String details;
    
    // Constructors
    public TaskExecutionLog() {}
    
    public TaskExecutionLog(String taskName, String instanceId, String status, String details) {
        this.taskName = taskName;
        this.instanceId = instanceId;
        this.status = status;
        this.details = details;
        this.executionTime = LocalDateTime.now();
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
