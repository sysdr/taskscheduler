package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableAsync
@EnableScheduling
public class SpringRetryTaskSchedulerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringRetryTaskSchedulerApplication.class, args);
    }
}
