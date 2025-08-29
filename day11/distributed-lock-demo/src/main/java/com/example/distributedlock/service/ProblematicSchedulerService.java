package com.example.distributedlock.service;

import com.example.distributedlock.model.TaskExecutionLog;
import com.example.distributedlock.model.TaskExecutionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProblematicSchedulerService {
    
    @Autowired
    private TaskExecutionLogRepository logRepository;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    private final AtomicInteger executionCounter = new AtomicInteger(0);
    private final Random random = new Random();
    
    // This task simulates a critical business operation that should run only once
    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void criticalBusinessTask() {
        String instanceId = "Instance-" + serverPort;
        String taskName = "CriticalBusinessTask";
        
        System.out.println("🔄 [" + instanceId + "] Starting critical business task...");
        
        try {
            // Simulate some processing time
            Thread.sleep(2000 + random.nextInt(3000)); // 2-5 seconds
            
            int executionNumber = executionCounter.incrementAndGet();
            String details = String.format("Execution #%d completed by %s", executionNumber, instanceId);
            
            // Log the execution
            TaskExecutionLog log = new TaskExecutionLog(taskName, instanceId, "COMPLETED", details);
            logRepository.save(log);
            
            System.out.println("✅ [" + instanceId + "] " + details);
            
        } catch (Exception e) {
            TaskExecutionLog log = new TaskExecutionLog(taskName, instanceId, "FAILED", e.getMessage());
            logRepository.save(log);
            System.out.println("❌ [" + instanceId + "] Task failed: " + e.getMessage());
        }
    }
    
    // This task simulates daily financial calculations
    @Scheduled(fixedRate = 15000) // Every 15 seconds
    public void dailyFinancialCalculation() {
        String instanceId = "Instance-" + serverPort;
        String taskName = "DailyFinancialCalculation";
        
        System.out.println("💰 [" + instanceId + "] Starting daily financial calculation...");
        
        try {
            // Simulate financial calculation processing
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3 seconds
            
            double calculatedAmount = random.nextDouble() * 10000;
            String details = String.format("Calculated daily interest: $%.2f by %s", calculatedAmount, instanceId);
            
            TaskExecutionLog log = new TaskExecutionLog(taskName, instanceId, "COMPLETED", details);
            logRepository.save(log);
            
            System.out.println("✅ [" + instanceId + "] " + details);
            
        } catch (Exception e) {
            TaskExecutionLog log = new TaskExecutionLog(taskName, instanceId, "FAILED", e.getMessage());
            logRepository.save(log);
            System.out.println("❌ [" + instanceId + "] Financial calculation failed: " + e.getMessage());
        }
    }
}
