package com.taskscheduler.cronvalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CronValidatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CronValidatorApplication.class, args);
    }
}
