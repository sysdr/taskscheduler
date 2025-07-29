package com.taskscheduler.hello;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Hello Scheduler Service - Your First Scheduled Task
 * 
 * This service demonstrates the basics of task scheduling using Spring Boot's
 * @Scheduled annotation. It implements the requirements for Day 1:
 * - Print message every 10 seconds
 * - Include timestamp
 * - Show execution counter
 * - Auto-shutdown after 60 seconds
 */
@Service
public class HelloSchedulerService {
    
    private int executionCount = 0;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private long startTime;
    
    /**
     * Initialize the service when the application is ready
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        startTime = System.currentTimeMillis();
        System.out.println("✅ Hello Scheduler Service initialized at " + getCurrentTimestamp());
        System.out.println("📋 Will run for 60 seconds, executing every 10 seconds");
        System.out.println("⏰ Expected executions: 6");
        System.out.println();
    }
    
    /**
     * The main scheduled task - runs every 10 seconds
     * fixedRate means the task executes every 10000ms regardless of how long the previous execution took
     */
    @Scheduled(fixedRate = 10000)
    public void executeHelloTask() {
        // Check if we should continue running (60 seconds limit)
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - startTime) / 1000;
        
        if (elapsedSeconds >= 60) {
            System.out.println("⏹️  60 seconds elapsed. Shutting down gracefully...");
            System.out.println("📊 Total executions completed: " + executionCount);
            System.exit(0);
            return;
        }
        
        // Increment counter and execute the task
        executionCount++;
        
        String timestamp = getCurrentTimestamp();
        String message = String.format(
            "🎯 Hello from Task Scheduler! | Execution #%d | Time: %s | Elapsed: %ds",
            executionCount, timestamp, elapsedSeconds
        );
        
        System.out.println(message);
        
        // Add some additional useful information
        if (executionCount == 1) {
            System.out.println("   ↳ This is your first scheduled task execution!");
        } else if (executionCount == 6) {
            System.out.println("   ↳ Final execution - application will shutdown soon!");
        }
    }
    
    /**
     * Helper method to get current timestamp as formatted string
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(formatter);
    }
    
    /**
     * Getter for testing purposes
     */
    public int getExecutionCount() {
        return executionCount;
    }
}
