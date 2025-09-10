package com.taskscheduler.service;

import com.taskscheduler.model.LeaderElection;
import com.taskscheduler.repository.LeaderElectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class LeaderElectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(LeaderElectionService.class);
    
    private final LeaderElectionRepository repository;
    private final String serviceName;
    private final int heartbeatIntervalMs;
    private final int leaseDurationMs;
    private final String instanceId;
    private final AtomicBoolean isLeader = new AtomicBoolean(false);
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    
    public LeaderElectionService(LeaderElectionRepository repository,
                               @Value("${leader.election.service-name:task-scheduler}") String serviceName,
                               @Value("${leader.election.heartbeat-interval-ms:5000}") int heartbeatIntervalMs,
                               @Value("${leader.election.lease-duration-ms:15000}") int leaseDurationMs) {
        this.repository = repository;
        this.serviceName = serviceName;
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.leaseDurationMs = leaseDurationMs;
        this.instanceId = generateInstanceId();
    }
    
    @PostConstruct
    public void initialize() {
        logger.info("Initializing Leader Election Service for service: {}, instance: {}", 
                   serviceName, instanceId);
        logger.info("Heartbeat interval: {}ms, Lease duration: {}ms", 
                   heartbeatIntervalMs, leaseDurationMs);
    }
    
    @Scheduled(fixedDelayString = "${leader.election.heartbeat-interval-ms:5000}")
    public void maintainLeadership() {
        if (!isRunning.get()) {
            return;
        }
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = now.plusNanos(leaseDurationMs * 1_000_000L);
            
            int result = repository.tryAcquireOrRenewLease(
                serviceName, instanceId, expiresAt, heartbeatIntervalMs, now);
            
            // Check if we successfully acquired/renewed the lease
            boolean newLeaderStatus = checkLeadershipStatus();
            
            if (newLeaderStatus && !isLeader.get()) {
                logger.info("✅ Acquired leadership for service: {}, instance: {}", serviceName, instanceId);
                isLeader.set(true);
            } else if (!newLeaderStatus && isLeader.get()) {
                logger.warn("❌ Lost leadership for service: {}, instance: {}", serviceName, instanceId);
                isLeader.set(false);
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("Leadership maintenance - Service: {}, Instance: {}, IsLeader: {}", 
                           serviceName, instanceId, isLeader.get());
            }
            
        } catch (Exception e) {
            logger.error("Error during leadership maintenance for instance: " + instanceId, e);
            isLeader.set(false);
        }
    }
    
    private boolean checkLeadershipStatus() {
        try {
            Optional<LeaderElection> currentLeader = repository.findByServiceName(serviceName);
            if (currentLeader.isPresent()) {
                LeaderElection leader = currentLeader.get();
                LocalDateTime now = LocalDateTime.now();
                
                // Check if lease is not expired and we are the leader
                return leader.getLeaseExpiresAt().isAfter(now) && 
                       instanceId.equals(leader.getLeaderInstanceId());
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking leadership status", e);
            return false;
        }
    }
    
    public boolean isLeader() {
        return isLeader.get() && isRunning.get();
    }
    
    public String getCurrentLeader() {
        try {
            Optional<LeaderElection> currentLeader = repository.findByServiceName(serviceName);
            if (currentLeader.isPresent()) {
                LeaderElection leader = currentLeader.get();
                LocalDateTime now = LocalDateTime.now();
                
                if (leader.getLeaseExpiresAt().isAfter(now)) {
                    return leader.getLeaderInstanceId();
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Error getting current leader", e);
            return null;
        }
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down Leader Election Service for instance: {}", instanceId);
        isRunning.set(false);
        
        if (isLeader.get()) {
            try {
                repository.releaseLease(serviceName, instanceId);
                logger.info("Released leadership lease for instance: {}", instanceId);
            } catch (Exception e) {
                logger.error("Error releasing leadership lease", e);
            }
        }
        
        isLeader.set(false);
    }
    
    private String generateInstanceId() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            long timestamp = System.currentTimeMillis();
            return hostname + "-" + timestamp + "-" + Thread.currentThread().getId();
        } catch (Exception e) {
            logger.warn("Could not determine hostname, using fallback instance ID", e);
            return "instance-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
        }
    }
}
