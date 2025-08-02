package com.taskscheduler.service;

import com.taskscheduler.model.TaskExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerService.class);
    
    private final ConcurrentLinkedQueue<TaskExecution> executionHistory = new ConcurrentLinkedQueue<>();
    private final AtomicInteger healthCheckCount = new AtomicInteger(0);
    private final AtomicInteger cleanupCount = new AtomicInteger(0);
    private final AtomicInteger reportCount = new AtomicInteger(0);
    
    // Fixed Rate: System Health Check (every 5 seconds)
    @Scheduled(fixedRate = 5000)
    public void systemHealthCheck() {
        long startTime = System.currentTimeMillis();
        LocalDateTime executionTime = LocalDateTime.now();
        
        try {
            // Simulate health check operations
            Thread.sleep(1000 + (long)(Math.random() * 2000)); // 1-3 seconds
            
            int count = healthCheckCount.incrementAndGet();
            long duration = System.currentTimeMillis() - startTime;
            
            TaskExecution execution = new TaskExecution(
                "System Health Check", 
                "FIXED_RATE", 
                executionTime, 
                "SUCCESS", 
                duration
            );
            
            addExecution(execution);
            logger.info("✅ Health Check #{} completed in {}ms - Status: HEALTHY", count, duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TaskExecution execution = new TaskExecution(
                "System Health Check", 
                "FIXED_RATE", 
                executionTime, 
                "ERROR", 
                duration
            );
            addExecution(execution);
            logger.error("❌ Health check failed", e);
        }
    }
    
    // Fixed Delay: Cleanup Tasks (15 seconds after completion)
    @Scheduled(fixedDelay = 15000)
    public void performCleanup() {
        long startTime = System.currentTimeMillis();
        LocalDateTime executionTime = LocalDateTime.now();
        
        try {
            // Simulate cleanup operations with variable duration
            Thread.sleep(3000 + (long)(Math.random() * 5000)); // 3-8 seconds
            
            int count = cleanupCount.incrementAndGet();
            long duration = System.currentTimeMillis() - startTime;
            
            TaskExecution execution = new TaskExecution(
                "System Cleanup", 
                "FIXED_DELAY", 
                executionTime, 
                "SUCCESS", 
                duration
            );
            
            addExecution(execution);
            logger.info("🧹 Cleanup Task #{} completed in {}ms - Temporary files cleaned", count, duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TaskExecution execution = new TaskExecution(
                "System Cleanup", 
                "FIXED_DELAY", 
                executionTime, 
                "ERROR", 
                duration
            );
            addExecution(execution);
            logger.error("❌ Cleanup task failed", e);
        }
    }
    
    // Cron: Daily Report Generation (every minute for demo - normally daily)
    @Scheduled(cron = "0 * * * * ?") // Every minute for demo purposes
    public void generateDailyReport() {
        long startTime = System.currentTimeMillis();
        LocalDateTime executionTime = LocalDateTime.now();
        
        try {
            // Simulate report generation
            Thread.sleep(2000 + (long)(Math.random() * 3000)); // 2-5 seconds
            
            int count = reportCount.incrementAndGet();
            long duration = System.currentTimeMillis() - startTime;
            
            TaskExecution execution = new TaskExecution(
                "Daily Report Generation", 
                "CRON", 
                executionTime, 
                "SUCCESS", 
                duration
            );
            
            addExecution(execution);
            logger.info("📊 Daily Report #{} generated in {}ms - Analytics updated", count, duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TaskExecution execution = new TaskExecution(
                "Daily Report Generation", 
                "CRON", 
                executionTime, 
                "ERROR", 
                duration
            );
            addExecution(execution);
            logger.error("❌ Report generation failed", e);
        }
    }
    
    private void addExecution(TaskExecution execution) {
        executionHistory.offer(execution);
        // Keep only last 50 executions
        while (executionHistory.size() > 50) {
            executionHistory.poll();
        }
    }
    
    public List<TaskExecution> getExecutionHistory() {
        return new ArrayList<>(executionHistory);
    }
    
    public int getHealthCheckCount() { return healthCheckCount.get(); }
    public int getCleanupCount() { return cleanupCount.get(); }
    public int getReportCount() { return reportCount.get(); }
}
