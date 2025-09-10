package com.taskscheduler.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "leader_election")
public class LeaderElection {
    
    @Id
    @Column(name = "service_name", length = 100)
    private String serviceName;
    
    @NotNull
    @Column(name = "leader_instance_id")
    private String leaderInstanceId;
    
    @NotNull
    @Column(name = "lease_expires_at")
    private LocalDateTime leaseExpiresAt;
    
    @NotNull
    @Column(name = "heartbeat_interval_ms")
    private Integer heartbeatIntervalMs;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public LeaderElection() {}
    
    public LeaderElection(String serviceName, String leaderInstanceId, 
                         LocalDateTime leaseExpiresAt, Integer heartbeatIntervalMs) {
        this.serviceName = serviceName;
        this.leaderInstanceId = leaderInstanceId;
        this.leaseExpiresAt = leaseExpiresAt;
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getLeaderInstanceId() { return leaderInstanceId; }
    public void setLeaderInstanceId(String leaderInstanceId) { this.leaderInstanceId = leaderInstanceId; }
    
    public LocalDateTime getLeaseExpiresAt() { return leaseExpiresAt; }
    public void setLeaseExpiresAt(LocalDateTime leaseExpiresAt) { this.leaseExpiresAt = leaseExpiresAt; }
    
    public Integer getHeartbeatIntervalMs() { return heartbeatIntervalMs; }
    public void setHeartbeatIntervalMs(Integer heartbeatIntervalMs) { this.heartbeatIntervalMs = heartbeatIntervalMs; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
