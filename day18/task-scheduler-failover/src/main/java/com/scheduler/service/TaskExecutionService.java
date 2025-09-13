package com.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskExecutionService {
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionService.class);
    
    private final LeaderElectionService leaderElectionService;
    private final AtomicLong taskCounter = new AtomicLong(0);
    
    public TaskExecutionService(LeaderElectionService leaderElectionService) {
        this.leaderElectionService = leaderElectionService;
    }
    
    @Scheduled(fixedDelay = 10000) // Every 10 seconds
    public void processCriticalTasks() {
        if (!leaderElectionService.isLeader()) {
            logger.debug("Skipping task processing - not the leader");
            return;
        }
        
        long taskId = taskCounter.incrementAndGet();
        logger.info("🔥 LEADER PROCESSING CRITICAL TASK #{} - Node: {}", 
                taskId, leaderElectionService.getNodeId());
        
        // Simulate task processing
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3000));
            logger.info("✅ Completed critical task #{}", taskId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Task processing interrupted");
        }
    }
    
    @Scheduled(fixedDelay = 5000) // Every 5 seconds  
    public void processScheduledJobs() {
        if (!leaderElectionService.isLeader()) {
            return;
        }
        
        long jobId = taskCounter.incrementAndGet();
        logger.info("⚡ Executing scheduled job #{} - Leader: {}", 
                jobId, leaderElectionService.getNodeId());
    }
}
