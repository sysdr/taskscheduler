package com.taskscheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskProcessorService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessorService.class);
    
    private final LeaderElectionService leaderElectionService;
    private final AtomicLong taskCounter = new AtomicLong(0);
    
    public TaskProcessorService(LeaderElectionService leaderElectionService) {
        this.leaderElectionService = leaderElectionService;
    }
    
    @Scheduled(fixedDelay = 3000) // Process tasks every 3 seconds
    public void processTasks() {
        if (leaderElectionService.isLeader()) {
            long taskId = taskCounter.incrementAndGet();
            logger.info("🚀 Processing task #{} at {} (Leader: {})", 
                       taskId, LocalDateTime.now(), leaderElectionService.getInstanceId());
            
            // Simulate task processing
            try {
                Thread.sleep(500); // Simulate work
                logger.info("✅ Completed task #{}", taskId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Task #{} interrupted", taskId);
            }
        } else {
            logger.debug("⏸️ Not leader, skipping task processing (Current leader: {})", 
                        leaderElectionService.getCurrentLeader());
        }
    }
    
    public long getProcessedTaskCount() {
        return taskCounter.get();
    }
}
