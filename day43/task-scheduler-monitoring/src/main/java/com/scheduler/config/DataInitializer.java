package com.scheduler.config;

import com.scheduler.model.Task;
import com.scheduler.model.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final TaskRepository taskRepository;
    private final Random random = new Random();
    
    @Override
    public void run(String... args) {
        if (taskRepository.count() > 0) {
            log.info("Database already contains data, skipping demo data initialization");
            return;
        }
        
        log.info("Initializing demo data...");
        
        String[] types = {"EMAIL", "REPORT", "DATA_SYNC", "BACKUP"};
        String[] taskNames = {
            "Send Weekly Newsletter",
            "Generate Sales Report",
            "Sync User Database",
            "Backup Production Data",
            "Send Invoice Emails",
            "Monthly Analytics Report",
            "Sync Customer Records",
            "Backup Configuration Files",
            "Send Welcome Emails",
            "Quarterly Financial Report",
            "Sync Product Catalog",
            "Backup Application Logs",
            "Send Password Reset Emails",
            "Daily Performance Report",
            "Sync Inventory Data",
            "Backup User Profiles",
            "Send Order Confirmations",
            "Weekly Summary Report",
            "Sync Payment Gateway",
            "Backup Transaction History"
        };
        
        LocalDateTime now = LocalDateTime.now();
        
        // Create completed tasks (60%)
        for (int i = 0; i < 12; i++) {
            Task task = createTask(
                taskNames[i],
                types[random.nextInt(types.length)],
                Task.TaskStatus.COMPLETED,
                now.minusMinutes(random.nextInt(120)),
                now.minusMinutes(random.nextInt(60)),
                (long)(100 + random.nextInt(2000))
            );
            taskRepository.save(task);
        }
        
        // Create failed tasks (15%)
        for (int i = 12; i < 15; i++) {
            Task task = createTask(
                taskNames[i],
                types[random.nextInt(types.length)],
                Task.TaskStatus.FAILED,
                now.minusMinutes(random.nextInt(120)),
                now.minusMinutes(random.nextInt(60)),
                (long)(50 + random.nextInt(500))
            );
            task.setErrorMessage("Task execution failed: " + 
                new String[]{"Connection timeout", "Invalid data format", "Resource unavailable", "Permission denied"}[random.nextInt(4)]);
            taskRepository.save(task);
        }
        
        // Create running tasks (10%)
        for (int i = 15; i < 17; i++) {
            Task task = createTask(
                taskNames[i],
                types[random.nextInt(types.length)],
                Task.TaskStatus.RUNNING,
                now.minusMinutes(random.nextInt(10)),
                now.minusSeconds(random.nextInt(60)),
                null
            );
            taskRepository.save(task);
        }
        
        // Create pending tasks (15%)
        for (int i = 17; i < 20; i++) {
            Task task = createTask(
                taskNames[i],
                types[random.nextInt(types.length)],
                Task.TaskStatus.PENDING,
                now.minusSeconds(random.nextInt(300)),
                null,
                null
            );
            taskRepository.save(task);
        }
        
        log.info("Demo data initialized: {} tasks created", taskRepository.count());
    }
    
    private Task createTask(String name, String type, Task.TaskStatus status, 
                            LocalDateTime createdAt, LocalDateTime startedAt, Long durationMs) {
        Task task = new Task();
        task.setName(name);
        task.setType(type);
        task.setStatus(status);
        task.setCreatedAt(createdAt);
        task.setStartedAt(startedAt);
        
        if (status == Task.TaskStatus.COMPLETED || status == Task.TaskStatus.FAILED) {
            task.setCompletedAt(startedAt != null ? startedAt.plusSeconds(durationMs != null ? durationMs / 1000 : 1) : createdAt.plusSeconds(1));
            task.setDurationMs(durationMs);
        }
        
        return task;
    }
}

