package com.taskscheduler.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TaskConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskConsumerApplication.class, args);
    }
}
