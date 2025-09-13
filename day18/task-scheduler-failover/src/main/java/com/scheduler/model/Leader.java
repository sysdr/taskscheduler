package com.scheduler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "leaders")
public class Leader {
    @Id
    private String id = "SINGLETON_LEADER";
    
    @Column(name = "node_id", nullable = false)
    private String nodeId;
    
    @Column(name = "lease_expires_at", nullable = false)
    private LocalDateTime leaseExpiresAt;
    
    @Column(name = "generation", nullable = false)
    private Long generation = 1L;
    
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;
    
    @Version
    private Long version;
    
    // Constructors
    public Leader() {}
    
    public Leader(String nodeId, LocalDateTime leaseExpiresAt) {
        this.nodeId = nodeId;
        this.leaseExpiresAt = leaseExpiresAt;
        this.lastHeartbeat = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    
    public LocalDateTime getLeaseExpiresAt() { return leaseExpiresAt; }
    public void setLeaseExpiresAt(LocalDateTime leaseExpiresAt) { this.leaseExpiresAt = leaseExpiresAt; }
    
    public Long getGeneration() { return generation; }
    public void setGeneration(Long generation) { this.generation = generation; }
    
    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(leaseExpiresAt);
    }
    
    public void renewLease(int leaseDurationSeconds) {
        this.leaseExpiresAt = LocalDateTime.now().plusSeconds(leaseDurationSeconds);
        this.lastHeartbeat = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leader leader = (Leader) o;
        return Objects.equals(id, leader.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
