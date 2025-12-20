package com.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PerformanceTunedSchedulerApplication {
    public static void main(String[] args) {
        System.out.println("🚀 Starting Performance Tuned Scheduler...");
        System.out.println("📊 JMX Port: 9010 (for JVisualVM)");
        System.out.println("🌐 Dashboard: http://localhost:8058");
        SpringApplication.run(PerformanceTunedSchedulerApplication.class, args);
    }
}
