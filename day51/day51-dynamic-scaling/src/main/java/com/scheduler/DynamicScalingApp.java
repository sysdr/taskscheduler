package com.scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication @EnableScheduling
public class DynamicScalingApp {
    public static void main(String[] args) {
        SpringApplication.run(DynamicScalingApp.class, args);
        System.out.println("\n✅ Dynamic Scaling Scheduler Running!");
        System.out.println("Dashboard: http://localhost:3000");
        System.out.println("Metrics: http://localhost:8080/actuator/prometheus\n");
    }
}
