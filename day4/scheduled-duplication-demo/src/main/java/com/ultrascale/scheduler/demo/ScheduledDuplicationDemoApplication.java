package com.ultrascale.scheduler.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScheduledDuplicationDemoApplication {
    
    public static void main(String[] args) {
        // Set unique instance ID for demonstration
        String instanceId = System.getProperty("instance.id", "INSTANCE-" + System.currentTimeMillis());
        System.setProperty("app.instance.id", instanceId);
        
        SpringApplication.run(ScheduledDuplicationDemoApplication.class, args);
    }
}
