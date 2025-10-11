package com.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableAsync
@EnableScheduling
public class TaskSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerApplication.class, args);
        System.out.println("🔥 Task Scheduler with DLQ Pattern Started!");
        System.out.println("📊 Dashboard: http://localhost:8080");
        System.out.println("🔍 DLQ Monitor: http://localhost:8080/dlq");
        System.out.println("📈 Metrics: http://localhost:8080/actuator/metrics");
    }
}
