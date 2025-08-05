package com.taskscheduler.service;

import com.taskscheduler.model.TaskExecutionMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ScheduledTasksService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksService.class);
    private final Map<String, TaskExecutionMetrics> metricsMap = new ConcurrentHashMap<>();
    
    @Scheduled(fixedRate = 2000) // Every 2 seconds
    public void quickTask() {
        executeTask("quickTask", 100, 300);
    }
    
    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void mediumTask() {
        executeTask("mediumTask", 500, 1000);
    }
    
    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void slowTask() {
        executeTask("slowTask", 1000, 3000);
    }
    
    @Scheduled(cron = "0/15 * * * * *") // Every 15 seconds
    public void cronTask() {
        executeTask("cronTask", 200, 800);
    }
    
    @Scheduled(fixedDelay = 3000, initialDelay = 1000) // 3 seconds after completion
    public void delayedTask() {
        executeTask("delayedTask", 300, 700);
    }
    
    @Scheduled(fixedRate = 1000) // Every second - high frequency
    public void highFrequencyTask() {
        executeTask("highFrequencyTask", 50, 150);
    }
    
    private void executeTask(String taskName, int minDuration, int maxDuration) {
        long startTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        
        try {
            // Simulate work with random duration
            int duration = ThreadLocalRandom.current().nextInt(minDuration, maxDuration + 1);
            Thread.sleep(duration);
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Record metrics
            metricsMap.computeIfAbsent(taskName, TaskExecutionMetrics::new)
                     .recordExecution(executionTime, threadName);
            
            logger.debug("Task '{}' executed in {}ms by thread '{}'", taskName, executionTime, threadName);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Task '{}' was interrupted", taskName, e);
        } catch (Exception e) {
            logger.error("Error executing task '{}'", taskName, e);
        }
    }
    
    public Map<String, TaskExecutionMetrics> getMetrics() {
        return Map.copyOf(metricsMap);
    }
}
