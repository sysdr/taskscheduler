package com.scheduler.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskSchedulerApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerApiApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("🚀 Task Scheduler API Started Successfully!");
        System.out.println("===========================================");
        System.out.println("📊 Dashboard: http://localhost:8080");
        System.out.println("🔧 API Base: http://localhost:8080/api/v1");
        System.out.println("💾 H2 Console: http://localhost:8080/h2-console");
        System.out.println("📈 Actuator: http://localhost:8080/actuator");
        System.out.println("===========================================\n");
    }
}
