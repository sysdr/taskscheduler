package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ThreadPoolSchedulerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ThreadPoolSchedulerApplication.class, args);
    }
}
