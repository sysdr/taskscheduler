package com.scheduler.service;

import com.scheduler.model.NodeHealth;
import com.scheduler.repository.NodeHealthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import com.sun.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HealthMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(HealthMonitorService.class);
    
    private final NodeHealthRepository nodeHealthRepository;
    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;
    
    @Value("${scheduler.node.id}")
    private String nodeId;
    
    @Value("${scheduler.leader.heartbeat-interval:5000}")
    private int heartbeatInterval;
    
    public HealthMonitorService(NodeHealthRepository nodeHealthRepository) {
        this.nodeHealthRepository = nodeHealthRepository;
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
    }
    
    @Scheduled(fixedDelayString = "${scheduler.leader.heartbeat-interval}")
    @Transactional
    public void updateNodeHealth() {
        try {
            NodeHealth health = nodeHealthRepository.findById(nodeId)
                    .orElse(new NodeHealth(nodeId));
            
            // Update system metrics
            health.setCpuUsage(getCpuUsage());
            health.setMemoryUsage(getMemoryUsage());
            health.setLastHeartbeat(LocalDateTime.now());
            health.setStatus(calculateHealthStatus(health));
            
            nodeHealthRepository.save(health);
            
            logger.debug("Updated health for node {}: CPU={}%, Memory={}%, Status={}", 
                    nodeId, health.getCpuUsage(), health.getMemoryUsage(), health.getStatus());
                    
        } catch (Exception e) {
            logger.error("Error updating node health", e);
        }
    }
    
    private double getCpuUsage() {
        try {
            double load = osBean.getProcessCpuLoad();
            return load >= 0 ? load * 100 : 0.0;
        } catch (Exception e) {
            logger.debug("Could not get CPU usage", e);
            return 0.0;
        }
    }
    
    private double getMemoryUsage() {
        try {
            long used = memoryBean.getHeapMemoryUsage().getUsed();
            long max = memoryBean.getHeapMemoryUsage().getMax();
            return max > 0 ? (used * 100.0) / max : 0.0;
        } catch (Exception e) {
            logger.debug("Could not get memory usage", e);
            return 0.0;
        }
    }
    
    private NodeHealth.HealthStatus calculateHealthStatus(NodeHealth health) {
        double cpu = health.getCpuUsage() != null ? health.getCpuUsage() : 0.0;
        double memory = health.getMemoryUsage() != null ? health.getMemoryUsage() : 0.0;
        
        if (cpu > 90 || memory > 95) {
            return NodeHealth.HealthStatus.UNHEALTHY;
        } else if (cpu > 70 || memory > 85) {
            return NodeHealth.HealthStatus.DEGRADED;
        } else {
            return NodeHealth.HealthStatus.HEALTHY;
        }
    }
    
    public boolean isNodeHealthy(String nodeId) {
        Optional<NodeHealth> health = nodeHealthRepository.findById(nodeId);
        if (health.isEmpty()) {
            return false;
        }
        
        NodeHealth nodeHealth = health.get();
        
        // Check if node is stale (missed several heartbeats)
        if (nodeHealth.isStale(heartbeatInterval * 3 / 1000)) {
            logger.warn("Node {} is stale, last heartbeat: {}", nodeId, nodeHealth.getLastHeartbeat());
            return false;
        }
        
        // Check health status
        return nodeHealth.getStatus() == NodeHealth.HealthStatus.HEALTHY ||
               nodeHealth.getStatus() == NodeHealth.HealthStatus.DEGRADED;
    }
    
    public List<NodeHealth> getHealthyNodes() {
        return nodeHealthRepository.findHealthyNodes().stream()
                .filter(health -> !health.isStale(heartbeatInterval * 3 / 1000))
                .toList();
    }
    
    public List<NodeHealth> getAllNodeHealth() {
        return nodeHealthRepository.findAll();
    }
}
