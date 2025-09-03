package com.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskProcessor.class);
    
    private final TaskProcessingService taskProcessingService;
    
    public ScheduledTaskProcessor(TaskProcessingService taskProcessingService) {
        this.taskProcessingService = taskProcessingService;
    }
    
    @Scheduled(fixedDelay = 5000) // Process every 5 seconds
    public void processPendingTasks() {
        logger.debug("Scheduled task processing started");
        try {
            taskProcessingService.processPendingTasks();
        } catch (Exception e) {
            logger.error("Error in scheduled task processing", e);
        }
    }
}
