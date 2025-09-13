package com.scheduler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "node_health")
public class NodeHealth {
    @Id
    private String nodeId;
    
    @Enumerated(EnumType.STRING)
    private HealthStatus status = HealthStatus.HEALTHY;
    
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;
    
    @Column(name = "cpu_usage")
    private Double cpuUsage;
    
    @Column(name = "memory_usage")
    private Double memoryUsage;
    
    @Column(name = "active_tasks")
    private Integer activeTasks = 0;
    
    public enum HealthStatus {
        HEALTHY, DEGRADED, UNHEALTHY, UNKNOWN
    }
    
    // Constructors
    public NodeHealth() {}
    
    public NodeHealth(String nodeId) {
        this.nodeId = nodeId;
        this.lastHeartbeat = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    
    public HealthStatus getStatus() { return status; }
    public void setStatus(HealthStatus status) { this.status = status; }
    
    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    
    public Double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(Double cpuUsage) { this.cpuUsage = cpuUsage; }
    
    public Double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }
    
    public Integer getActiveTasks() { return activeTasks; }
    public void setActiveTasks(Integer activeTasks) { this.activeTasks = activeTasks; }
    
    public boolean isStale(int heartbeatTimeoutSeconds) {
        return lastHeartbeat == null || 
               LocalDateTime.now().isAfter(lastHeartbeat.plusSeconds(heartbeatTimeoutSeconds));
    }
}
