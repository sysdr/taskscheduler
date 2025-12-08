package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SecureTaskSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecureTaskSchedulerApplication.class, args);
    }
}
