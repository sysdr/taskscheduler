package com.scheduler.backpressure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackpressureSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackpressureSchedulerApplication.class, args);
    }
}
