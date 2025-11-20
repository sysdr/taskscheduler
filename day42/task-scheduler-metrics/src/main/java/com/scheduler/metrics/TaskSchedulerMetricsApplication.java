package com.scheduler.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskSchedulerMetricsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerMetricsApplication.class, args);
    }
}
