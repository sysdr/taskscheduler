package com.taskscheduler.config;

import com.taskscheduler.repository.TaskRepository;
import com.taskscheduler.service.DemoDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final TaskRepository taskRepository;
    private final DemoDataService demoDataService;
    
    @Override
    public void run(String... args) {
        // Generate demo data if database is empty
        if (taskRepository.count() == 0) {
            log.info("Database is empty. Generating demo data...");
            demoDataService.generateDemoData(50);
            log.info("Demo data generated successfully!");
        } else {
            log.info("Database already contains {} tasks. Skipping demo data generation.", 
                    taskRepository.count());
        }
    }
}

