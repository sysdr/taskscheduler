package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
public class TaskSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerApplication.class, args);
    }
}
