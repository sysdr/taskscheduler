package com.scheduler.leader.service;

import com.scheduler.leader.model.Leadership;
import com.scheduler.leader.repository.LeadershipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LeaderElectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(LeaderElectionService.class);
    
    private final LeadershipRepository leadershipRepository;
    private final AtomicBoolean isLeader = new AtomicBoolean(false);
    private final AtomicReference<String> currentState = new AtomicReference<>("STARTUP");
    
    @Value("${scheduler.instance.id}")
    private String instanceId;
    
    @Value("${scheduler.leader.lease-duration}")
    private long leaseDuration;
    
    @Value("${scheduler.leader.heartbeat-interval}")
    private long heartbeatInterval;
    
    public LeaderElectionService(LeadershipRepository leadershipRepository) {
        this.leadershipRepository = leadershipRepository;
    }
    
    @Scheduled(fixedDelayString = "${scheduler.leader.heartbeat-interval}")
    @Transactional
    public void maintainLeadership() {
        try {
            if (isLeader.get()) {
                renewLease();
            } else {
                attemptLeadershipAcquisition();
            }
        } catch (Exception e) {
            logger.error("Error in leadership maintenance", e);
            handleElectionFailure();
        }
    }
    
    private void attemptLeadershipAcquisition() {
        currentState.set("CANDIDATE");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime leaseEnd = now.plusSeconds(leaseDuration / 1000);
        
        // Try to create initial leadership record
        if (!leadershipRepository.findCurrentLeadership().isPresent()) {
            try {
                Leadership leadership = new Leadership(instanceId, now, leaseEnd, (int) heartbeatInterval);
                leadershipRepository.save(leadership);
                becomeLeader();
                return;
            } catch (Exception e) {
                logger.debug("Failed to create leadership record, attempting takeover");
            }
        }
        
        // Try to take over expired lease
        int updated = leadershipRepository.attemptLeadershipTakeover(instanceId, now, leaseEnd, now);
        if (updated > 0) {
            becomeLeader();
        } else {
            becomeFollower();
        }
    }
    
    private void renewLease() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newEnd = now.plusSeconds(leaseDuration / 1000);
        
        int renewed = leadershipRepository.renewLease(instanceId, newEnd, now);
        if (renewed == 0) {
            logger.warn("Failed to renew lease - lost leadership");
            loseLeadership();
        }
    }
    
    private void becomeLeader() {
        if (!isLeader.get()) {
            isLeader.set(true);
            currentState.set("LEADER");
            logger.info("✓ BECAME LEADER: Instance {} acquired leadership", instanceId);
        }
    }
    
    private void becomeFollower() {
        if (isLeader.get()) {
            loseLeadership();
        } else {
            currentState.set("FOLLOWER");
            logger.debug("Remaining as follower: {}", instanceId);
        }
    }
    
    private void loseLeadership() {
        isLeader.set(false);
        currentState.set("FOLLOWER");
        logger.warn("✗ LOST LEADERSHIP: Instance {} is now a follower", instanceId);
    }
    
    private void handleElectionFailure() {
        isLeader.set(false);
        currentState.set("FAILED");
        logger.error("✗ ELECTION FAILED: Instance {} in failed state", instanceId);
    }
    
    public boolean isLeader() {
        return isLeader.get();
    }
    
    public String getCurrentState() {
        return currentState.get();
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    @Transactional
    public void releaseLeadership() {
        if (isLeader.get()) {
            try {
                LocalDateTime now = LocalDateTime.now();
                leadershipRepository.attemptLeadershipTakeover("RELEASED", now, now, now);
                loseLeadership();
                logger.info("✓ Released leadership gracefully");
            } catch (Exception e) {
                logger.error("Error releasing leadership", e);
            }
        }
    }
}
