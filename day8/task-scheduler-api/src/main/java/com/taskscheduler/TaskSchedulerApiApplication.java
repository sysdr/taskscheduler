package com.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TaskSchedulerApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerApiApplication.class, args);
        System.out.println("\n🚀 Task Scheduler API is running!");
        System.out.println("📊 API Documentation: http://localhost:8080/api/tasks/health");
        System.out.println("🗄️  H2 Console: http://localhost:8080/api/h2-console");
        System.out.println("📈 Actuator: http://localhost:8080/api/actuator/health");
    }
}
