package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerApplication.class, args);
        System.out.println("🚀 Task Scheduler Day 2 Application Started!");
        System.out.println("📊 Dashboard: http://localhost:8080");
        System.out.println("📈 Metrics: http://localhost:8080/actuator/metrics");
    }
}
