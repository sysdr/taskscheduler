package com.scheduler;

import com.scheduler.model.TaskDefinition;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DistributedTaskSchedulerApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(DistributedTaskSchedulerApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(DistributedTaskSchedulerApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner initData(TaskDefinitionRepository repository) {
        return args -> {
            logger.info("Initializing sample task definitions...");
            
            // Create sample tasks
            if (repository.count() == 0) {
                repository.save(new TaskDefinition(
                    "daily-report",
                    "Generate daily system report",
                    "0 0 9 * * ?",
                    "MEDIUM_TASK"
                ));
                
                repository.save(new TaskDefinition(
                    "cleanup-temp-files",
                    "Clean up temporary files",
                    "0 0 2 * * ?",
                    "FAST_TASK"
                ));
                
                repository.save(new TaskDefinition(
                    "data-backup",
                    "Perform incremental data backup",
                    "0 0 1 * * ?",
                    "SLOW_TASK"
                ));
                
                logger.info("Sample tasks created successfully");
            }
        };
    }
}
