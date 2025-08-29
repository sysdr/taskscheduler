package com.example.distributedlock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DistributedLockDemoApplication {
    public static void main(String[] args) {
        // Enable different port for multiple instances
        String port = System.getProperty("server.port", "8080");
        System.setProperty("server.port", port);
        
        SpringApplication.run(DistributedLockDemoApplication.class, args);
    }
}
