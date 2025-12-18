package com.scheduler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_metrics")
public class TenantMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tenantId;
    
    private Integer currentRunningTasks = 0;
    private Integer tasksToday = 0;
    private LocalDateTime lastResetTime = LocalDateTime.now();
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public Integer getCurrentRunningTasks() { return currentRunningTasks; }
    public void setCurrentRunningTasks(Integer count) { this.currentRunningTasks = count; }
    
    public Integer getTasksToday() { return tasksToday; }
    public void setTasksToday(Integer count) { this.tasksToday = count; }
    
    public LocalDateTime getLastResetTime() { return lastResetTime; }
    public void setLastResetTime(LocalDateTime time) { this.lastResetTime = time; }
}
