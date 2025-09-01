package com.taskscheduler.config;

import com.taskscheduler.service.DistributedLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
    
    private final DistributedLockService lockService;
    
    public SchedulerConfig(DistributedLockService lockService) {
        this.lockService = lockService;
    }
    
    /**
     * Periodic cleanup of expired locks
     */
    @Scheduled(fixedRateString = "${app.distributed-locks.cleanup-interval-minutes:5}000")
    public void cleanupExpiredLocks() {
        try {
            int cleaned = lockService.cleanupExpiredLocks();
            if (cleaned > 0) {
                logger.info("Scheduled cleanup removed {} expired locks", cleaned);
            }
        } catch (Exception e) {
            logger.error("Error during scheduled lock cleanup", e);
        }
    }
}
