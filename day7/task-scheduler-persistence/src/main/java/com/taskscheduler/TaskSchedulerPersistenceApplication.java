package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TaskSchedulerPersistenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerPersistenceApplication.class, args);
    }
}
