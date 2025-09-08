package com.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RedlockTaskSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedlockTaskSchedulerApplication.class, args);
    }
}
