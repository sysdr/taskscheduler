package com.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TaskPrioritySchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskPrioritySchedulerApplication.class, args);
    }
}
