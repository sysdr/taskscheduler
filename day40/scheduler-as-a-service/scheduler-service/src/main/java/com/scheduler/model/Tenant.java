package com.scheduler.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String tenantId;
    private String tenantName;
    private String apiKey;
    private Integer maxConcurrentTasks;
    private Integer rateLimit; // tasks per minute
    private Boolean active;
    
    // Constructors
    public Tenant() {
        this.active = true;
        this.maxConcurrentTasks = 10;
        this.rateLimit = 100;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    
    public Integer getMaxConcurrentTasks() { return maxConcurrentTasks; }
    public void setMaxConcurrentTasks(Integer maxConcurrentTasks) { 
        this.maxConcurrentTasks = maxConcurrentTasks; 
    }
    
    public Integer getRateLimit() { return rateLimit; }
    public void setRateLimit(Integer rateLimit) { this.rateLimit = rateLimit; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
