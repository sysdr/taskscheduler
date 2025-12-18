package com.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MultiTenantSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MultiTenantSchedulerApplication.class, args);
    }
}
