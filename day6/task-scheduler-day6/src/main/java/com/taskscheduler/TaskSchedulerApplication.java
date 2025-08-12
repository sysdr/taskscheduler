package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Task Scheduler Day 6.
 * Demonstrates Task Definition Model design and implementation.
 */
@SpringBootApplication
public class TaskSchedulerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerApplication.class, args);
        System.out.println("\n🚀 Task Scheduler Day 6 Started Successfully!");
        System.out.println("📊 H2 Console: http://localhost:8080/h2-console");
        System.out.println("🔧 API Docs: http://localhost:8080/api/tasks");
        System.out.println("💡 Health Check: http://localhost:8080/actuator/health");
    }
}
