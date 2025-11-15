package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskExecutorService {
    
    private final TaskRepository taskRepository;
    private final MetricsService metricsService;
    private final Random random = new Random();

    @Async
    public void executeImageProcessingTask(Task task) {
        executeTask(task, () -> {
            log.info("Processing image from event {}", task.getTriggeredBy());
            simulateWork(1000, 2000);
            return "Image processed: metadata extracted, thumbnail generated";
        });
    }

    @Async
    public void executePdfProcessingTask(Task task) {
        executeTask(task, () -> {
            log.info("Processing PDF from event {}", task.getTriggeredBy());
            simulateWork(1500, 3000);
            return "PDF processed: text extracted, indexed";
        });
    }

    @Async
    public void executeGenericFileTask(Task task) {
        executeTask(task, () -> {
            log.info("Processing file from event {}", task.getTriggeredBy());
            simulateWork(500, 1500);
            return "File processed: metadata recorded";
        });
    }

    @Async
    public void executeSendEmailTask(Task task) {
        executeTask(task, () -> {
            log.info("Sending welcome email for event {}", task.getTriggeredBy());
            simulateWork(800, 1500);
            return "Welcome email sent successfully";
        });
    }

    @Async
    public void executeProfileInitTask(Task task) {
        executeTask(task, () -> {
            log.info("Initializing user profile for event {}", task.getTriggeredBy());
            simulateWork(600, 1200);
            return "User profile initialized with defaults";
        });
    }

    @Async
    public void executeAnalyticsTask(Task task) {
        executeTask(task, () -> {
            log.info("Recording analytics for event {}", task.getTriggeredBy());
            simulateWork(300, 800);
            return "Analytics event recorded";
        });
    }

    @Async
    public void executeProfileSyncTask(Task task) {
        executeTask(task, () -> {
            log.info("Syncing profile data for event {}", task.getTriggeredBy());
            simulateWork(500, 1000);
            return "Profile data synchronized";
        });
    }

    @Async
    public void executeCpuCleanupTask(Task task) {
        executeTask(task, () -> {
            log.info("Performing CPU cleanup for event {}", task.getTriggeredBy());
            simulateWork(2000, 3000);
            return "CPU cleanup completed: killed unnecessary processes";
        });
    }

    @Async
    public void executeDiskCleanupTask(Task task) {
        executeTask(task, () -> {
            log.info("Performing disk cleanup for event {}", task.getTriggeredBy());
            simulateWork(3000, 5000);
            return "Disk cleanup completed: freed 2.5GB";
        });
    }

    @Async
    public void executeArchivalTask(Task task) {
        executeTask(task, () -> {
            log.info("Archiving old files for event {}", task.getTriggeredBy());
            simulateWork(4000, 6000);
            return "Archived 1000 files to cold storage";
        });
    }

    @Async
    public void executeMemoryCleanupTask(Task task) {
        executeTask(task, () -> {
            log.info("Performing memory cleanup for event {}", task.getTriggeredBy());
            simulateWork(1500, 2500);
            return "Memory cleanup completed: garbage collection performed";
        });
    }

    private void executeTask(Task task, TaskLogic logic) {
        Instant startTime = Instant.now();
        task.setStartedAt(startTime);
        task.setStatus(TaskStatus.RUNNING);
        taskRepository.save(task);

        try {
            String result = logic.execute();
            
            Instant endTime = Instant.now();
            task.setCompletedAt(endTime);
            task.setExecutionTimeMs((int) Duration.between(startTime, endTime).toMillis());
            task.setStatus(TaskStatus.COMPLETED);
            task.setResult(result);
            
            metricsService.incrementTasksCompleted();
            
            log.info("Task {} completed successfully: {}", task.getTaskId(), result);
            
        } catch (Exception e) {
            Instant endTime = Instant.now();
            task.setCompletedAt(endTime);
            task.setExecutionTimeMs((int) Duration.between(startTime, endTime).toMillis());
            task.setStatus(TaskStatus.FAILED);
            task.setResult("Failed: " + e.getMessage());
            
            metricsService.incrementTasksFailed();
            
            log.error("Task {} failed", task.getTaskId(), e);
        }
        
        taskRepository.save(task);
    }

    private void simulateWork(int minMs, int maxMs) {
        try {
            int workTime = minMs + random.nextInt(maxMs - minMs);
            Thread.sleep(workTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    private interface TaskLogic {
        String execute() throws Exception;
    }
}
