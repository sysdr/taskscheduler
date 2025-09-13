package com.scheduler.service;

import com.scheduler.model.Leader;
import com.scheduler.model.NodeHealth;
import com.scheduler.repository.LeaderRepository;
import com.scheduler.repository.NodeHealthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LeaderElectionService {
    private static final Logger logger = LoggerFactory.getLogger(LeaderElectionService.class);
    
    private final LeaderRepository leaderRepository;
    private final NodeHealthRepository nodeHealthRepository;
    private final HealthMonitorService healthMonitorService;
    
    @Value("${scheduler.node.id}")
    private String nodeId;
    
    @Value("${scheduler.leader.lease-duration:15000}")
    private int leaseDuration;
    
    @Value("${scheduler.leader.heartbeat-interval:5000}")
    private int heartbeatInterval;
    
    private volatile boolean isLeader = false;
    private volatile boolean isCandidate = false;
    
    public LeaderElectionService(LeaderRepository leaderRepository,
                               NodeHealthRepository nodeHealthRepository,
                               HealthMonitorService healthMonitorService) {
        this.leaderRepository = leaderRepository;
        this.nodeHealthRepository = nodeHealthRepository;
        this.healthMonitorService = healthMonitorService;
    }
    
    @Scheduled(fixedDelayString = "${scheduler.leader.heartbeat-interval}")
    @Transactional
    public void maintainLeadership() {
        try {
            if (isLeader) {
                renewLeadership();
            } else {
                attemptLeadershipAcquisition();
            }
        } catch (Exception e) {
            logger.error("Error in leadership maintenance", e);
            isLeader = false;
        }
    }
    
    @Transactional
    public void renewLeadership() {
        try {
            Optional<Leader> currentLeader = leaderRepository.findCurrentLeader();
            
            if (currentLeader.isPresent() && nodeId.equals(currentLeader.get().getNodeId())) {
                Leader leader = currentLeader.get();
                leader.renewLease(leaseDuration / 1000);
                leaderRepository.save(leader);
                logger.debug("Leadership renewed for node: {}", nodeId);
            } else {
                logger.warn("Lost leadership, attempting re-election");
                isLeader = false;
                attemptLeadershipAcquisition();
            }
        } catch (OptimisticLockingFailureException e) {
            logger.warn("Leadership renewal failed due to concurrent modification");
            isLeader = false;
        }
    }
    
    @Transactional
    public void attemptLeadershipAcquisition() {
        if (isCandidate) {
            return; // Already trying to become leader
        }
        
        try {
            isCandidate = true;
            Optional<Leader> currentLeader = leaderRepository.findCurrentLeader();
            
            if (currentLeader.isEmpty() || currentLeader.get().isExpired()) {
                // No leader or current leader expired
                if (isEligibleForLeadership()) {
                    electNewLeader();
                }
            } else {
                // Check if current leader is still healthy
                String currentLeaderNodeId = currentLeader.get().getNodeId();
                if (!healthMonitorService.isNodeHealthy(currentLeaderNodeId)) {
                    logger.info("Current leader {} appears unhealthy, initiating failover", currentLeaderNodeId);
                    handleLeaderFailover(currentLeader.get());
                }
            }
        } finally {
            isCandidate = false;
        }
    }
    
    private boolean isEligibleForLeadership() {
        // Check if this node is healthy and capable of leadership
        Optional<NodeHealth> nodeHealth = nodeHealthRepository.findById(nodeId);
        return nodeHealth.map(health -> 
            health.getStatus() == NodeHealth.HealthStatus.HEALTHY &&
            (health.getCpuUsage() == null || health.getCpuUsage() < 80.0) &&
            (health.getMemoryUsage() == null || health.getMemoryUsage() < 85.0)
        ).orElse(true); // If no health record, assume healthy
    }
    
    private void electNewLeader() {
        try {
            // Add small random delay to prevent thundering herd
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
            
            Optional<Leader> existingLeader = leaderRepository.findCurrentLeader();
            
            if (existingLeader.isEmpty() || existingLeader.get().isExpired()) {
                Leader newLeader = new Leader(nodeId, LocalDateTime.now().plusSeconds(leaseDuration / 1000));
                
                if (existingLeader.isPresent()) {
                    newLeader.setGeneration(existingLeader.get().getGeneration() + 1);
                    newLeader.setVersion(existingLeader.get().getVersion());
                }
                
                leaderRepository.save(newLeader);
                isLeader = true;
                logger.info("Successfully elected as leader: {} with generation: {}", nodeId, newLeader.getGeneration());
            }
        } catch (Exception e) {
            logger.debug("Failed to become leader (expected in multi-node scenarios): {}", e.getMessage());
        }
    }
    
    private void handleLeaderFailover(Leader failedLeader) {
        logger.info("Handling failover from failed leader: {}", failedLeader.getNodeId());
        
        try {
            // Mark old leader as expired and increment generation
            failedLeader.setLeaseExpiresAt(LocalDateTime.now().minusSeconds(1));
            failedLeader.setGeneration(failedLeader.getGeneration() + 1);
            leaderRepository.save(failedLeader);
            
            // Attempt to become new leader
            electNewLeader();
        } catch (Exception e) {
            logger.error("Error during leader failover", e);
        }
    }
    
    public boolean isLeader() {
        return isLeader;
    }
    
    public Optional<Leader> getCurrentLeader() {
        return leaderRepository.findCurrentLeader();
    }
    
    public String getNodeId() {
        return nodeId;
    }
}
