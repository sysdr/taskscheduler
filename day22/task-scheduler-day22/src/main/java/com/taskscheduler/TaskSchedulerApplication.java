package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Task Scheduler Day 22
 * Focus: Task Status State Machine Implementation
 */
@SpringBootApplication
public class TaskSchedulerApplication {
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Task Scheduler - Day 22: Task Status State Machine");
        System.out.println("Dashboard: http://localhost:8080");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("API Base: http://localhost:8080/api/tasks");
        
        SpringApplication.run(TaskSchedulerApplication.class, args);
    }
}
