package com.scheduler.timezone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimezoneSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimezoneSchedulerApplication.class, args);
    }
}
