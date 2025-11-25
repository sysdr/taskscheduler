package com.scheduler.service;

import com.scheduler.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskSchedulerService {

    @Value("${scheduler.instance-id}")
    private String instanceId;

    private final Random random = new Random();
    private static final String[] TASK_TYPES = {"email", "report", "backup", "sync", "notification"};
    private static final String[] USER_IDS = {"user-001", "user-002", "user-003", "user-004"};

    @Scheduled(fixedRateString = "${scheduler.task-execution-rate}")
    public void executeScheduledTasks() {
        String taskId = "task-" + UUID.randomUUID().toString().substring(0, 8);
        String userId = USER_IDS[random.nextInt(USER_IDS.length)];
        String taskType = TASK_TYPES[random.nextInt(TASK_TYPES.length)];
        
        // Add to MDC for structured logging
        MDC.put("task_id", taskId);
        MDC.put("user_id", userId);
        
        try {
            Task task = Task.builder()
                .taskId(taskId)
                .userId(userId)
                .taskType(taskType)
                .scheduledTime(LocalDateTime.now())
                .build();
            
            log.info("Starting task execution: type={}, instance={}", taskType, instanceId);
            
            long startTime = System.currentTimeMillis();
            
            // Simulate task execution
            executeTask(task);
            
            long duration = System.currentTimeMillis() - startTime;
            task.setExecutionDurationMs((int) duration);
            task.setExecutionTime(LocalDateTime.now());
            
            log.info("Task completed successfully: duration={}ms", duration);
            
        } catch (Exception e) {
            log.error("Task execution failed: error={}", e.getMessage(), e);
        } finally {
            MDC.remove("task_id");
            MDC.remove("user_id");
        }
    }

    private void executeTask(Task task) throws InterruptedException {
        // Simulate work
        Thread.sleep(random.nextInt(500) + 100);
        
        // Simulate occasional failures
        if (random.nextInt(10) == 0) {
            throw new RuntimeException("Simulated task failure: " + 
                random.choice("Database timeout", "Network error", "Invalid data"));
        }
        
        log.debug("Task processing step completed: step=validation");
        Thread.sleep(random.nextInt(200));
        
        log.debug("Task processing step completed: step=execution");
        Thread.sleep(random.nextInt(200));
        
        log.debug("Task processing step completed: step=notification");
    }

    private static class Random extends java.util.Random {
        public String choice(String... options) {
            return options[nextInt(options.length)];
        }
    }
}
