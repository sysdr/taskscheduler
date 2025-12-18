package com.scheduler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String apiKey;
    
    private Integer maxConcurrentTasks = 5;
    private Integer maxTasksPerDay = 1000;
    private Boolean active = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    
    public Integer getMaxConcurrentTasks() { return maxConcurrentTasks; }
    public void setMaxConcurrentTasks(Integer max) { this.maxConcurrentTasks = max; }
    
    public Integer getMaxTasksPerDay() { return maxTasksPerDay; }
    public void setMaxTasksPerDay(Integer max) { this.maxTasksPerDay = max; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
