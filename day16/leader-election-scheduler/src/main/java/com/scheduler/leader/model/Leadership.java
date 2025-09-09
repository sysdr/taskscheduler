package com.scheduler.leader.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduler_leadership")
public class Leadership {
    
    @Id
    private String id = "SCHEDULER_LEADER";
    
    @Column(nullable = false)
    private String leaderId;
    
    @Column(nullable = false)
    private LocalDateTime leaseStart;
    
    @Column(nullable = false)
    private LocalDateTime leaseEnd;
    
    @Column(nullable = false)
    private int heartbeatInterval;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public Leadership() {}
    
    public Leadership(String leaderId, LocalDateTime leaseStart, LocalDateTime leaseEnd, int heartbeatInterval) {
        this.leaderId = leaderId;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
        this.heartbeatInterval = heartbeatInterval;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getLeaderId() { return leaderId; }
    public void setLeaderId(String leaderId) { this.leaderId = leaderId; }
    
    public LocalDateTime getLeaseStart() { return leaseStart; }
    public void setLeaseStart(LocalDateTime leaseStart) { this.leaseStart = leaseStart; }
    
    public LocalDateTime getLeaseEnd() { return leaseEnd; }
    public void setLeaseEnd(LocalDateTime leaseEnd) { this.leaseEnd = leaseEnd; }
    
    public int getHeartbeatInterval() { return heartbeatInterval; }
    public void setHeartbeatInterval(int heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(leaseEnd);
    }
}
