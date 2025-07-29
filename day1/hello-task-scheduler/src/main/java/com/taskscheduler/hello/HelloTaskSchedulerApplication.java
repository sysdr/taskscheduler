package com.taskscheduler.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello Task Scheduler Application - Day 1
 * 
 * This is our entry point into the world of task scheduling with Spring Boot.
 * The @EnableScheduling annotation activates Spring's scheduling capabilities.
 */
@SpringBootApplication
@EnableScheduling
public class HelloTaskSchedulerApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Starting Hello Task Scheduler Application...");
        
        // Configure the application to run for 60 seconds then shutdown
        System.setProperty("server.port", "8080");
        
        SpringApplication.run(HelloTaskSchedulerApplication.class, args);
    }
}
