package com.scheduler.task.service;

import com.scheduler.leader.service.LeaderElectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TaskExecutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionService.class);
    
    private final LeaderElectionService leaderElectionService;
    private int taskCounter = 0;
    
    public TaskExecutionService(LeaderElectionService leaderElectionService) {
        this.leaderElectionService = leaderElectionService;
    }
    
    @Scheduled(fixedDelay = 15000) // Every 15 seconds
    public void executeCriticalTask() {
        if (!leaderElectionService.isLeader()) {
            logger.debug("Skipping task execution - not leader ({})", leaderElectionService.getInstanceId());
            return;
        }
        
        taskCounter++;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        logger.info("🚀 EXECUTING CRITICAL TASK #{} at {} by leader {}", 
                   taskCounter, timestamp, leaderElectionService.getInstanceId());
        
        // Simulate some work
        try {
            Thread.sleep(2000);
            logger.info("✅ COMPLETED CRITICAL TASK #{} by leader {}", 
                       taskCounter, leaderElectionService.getInstanceId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Task execution interrupted", e);
        }
    }
    
    @Scheduled(fixedDelay = 30000) // Every 30 seconds  
    public void executeReportingTask() {
        if (!leaderElectionService.isLeader()) {
            return;
        }
        
        logger.info("📊 GENERATING REPORT by leader {}", leaderElectionService.getInstanceId());
        // Simulate report generation
        try {
            Thread.sleep(1000);
            logger.info("✅ REPORT COMPLETED by leader {}", leaderElectionService.getInstanceId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public int getTaskCounter() {
        return taskCounter;
    }
}
