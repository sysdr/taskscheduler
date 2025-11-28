package com.scheduler;

import com.scheduler.model.Task;
import com.scheduler.model.TaskType;
import com.scheduler.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class TaskSchedulerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TaskSchedulerApplication.class, args);
    }
    
    @Bean
    CommandLineRunner initData(TaskRepository taskRepository) {
        return args -> {
            // Create sample tasks
            taskRepository.save(Task.builder()
                .name("Daily Report Generation")
                .description("Generates daily analytics reports")
                .type(TaskType.CRON)
                .cronExpression("0 0 2 * * ?")
                .build());
            
            taskRepository.save(Task.builder()
                .name("Email Notification Processor")
                .description("Processes pending email notifications")
                .type(TaskType.FIXED_DELAY)
                .fixedDelayMs(300000L)
                .build());
            
            taskRepository.save(Task.builder()
                .name("Database Cleanup")
                .description("Cleans up old records")
                .type(TaskType.CRON)
                .cronExpression("0 0 3 * * ?")
                .build());
        };
    }
}
