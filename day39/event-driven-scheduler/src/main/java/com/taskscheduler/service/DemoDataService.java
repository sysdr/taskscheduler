package com.taskscheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.model.*;
import com.taskscheduler.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemoDataService {
    
    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;
    private final MetricsService metricsService;
    private final Random random = new Random();
    
    @Transactional
    public List<Task> generateDemoData(int count) {
        log.info("Generating {} demo tasks", count);
        List<Task> tasks = new ArrayList<>();
        Instant now = Instant.now();
        
        String[] fileNames = {
            "vacation-photo.jpg", "document.pdf", "presentation.pptx", 
            "spreadsheet.xlsx", "video.mp4", "archive.zip"
        };
        
        String[] userNames = {
            "john.doe", "jane.smith", "bob.wilson", "alice.brown", 
            "charlie.davis", "diana.miller"
        };
        
        String[] systemMetrics = {
            "CPU_HIGH", "DISK_LOW", "MEMORY_HIGH", "NETWORK_LATENCY"
        };
        
        for (int i = 0; i < count; i++) {
            Task task = new Task();
            task.setTaskId(UUID.randomUUID().toString());
            
            // Randomly assign event type and create appropriate task
            int eventTypeChoice = random.nextInt(3);
            Instant createdAt = now.minusSeconds(random.nextInt(3600)); // Random time in last hour
            
            switch (eventTypeChoice) {
                case 0 -> {
                    // File upload event
                    task.setEventType("FILE_UPLOAD");
                    task.setTaskName(generateFileTaskName());
                    task.setTaskType("FILE_PROCESSING");
                    metricsService.incrementFileEvents();
                    
                    FileUploadEvent fileEvent = new FileUploadEvent();
                    fileEvent.setFileName(fileNames[random.nextInt(fileNames.length)]);
                    fileEvent.setBucketName("user-uploads");
                    fileEvent.setFileType(getFileType(fileEvent.getFileName()));
                    fileEvent.setFileSize(1024L * (random.nextInt(10000) + 100));
                    fileEvent.setUploadedBy(userNames[random.nextInt(userNames.length)] + "@example.com");
                    
                    try {
                        task.setEventPayload(objectMapper.writeValueAsString(fileEvent));
                    } catch (Exception e) {
                        log.error("Failed to serialize file event", e);
                    }
                }
                case 1 -> {
                    // User action event
                    task.setEventType("USER_ACTION");
                    task.setTaskName(generateUserTaskName());
                    task.setTaskType(getUserTaskType());
                    metricsService.incrementUserEvents();
                    
                    UserActionEvent userEvent = new UserActionEvent();
                    userEvent.setUserId(UUID.randomUUID().toString());
                    userEvent.setUsername(userNames[random.nextInt(userNames.length)]);
                    userEvent.setActionType(getRandomActionType());
                    userEvent.setEmail(userEvent.getUsername() + "@example.com");
                    
                    try {
                        task.setEventPayload(objectMapper.writeValueAsString(userEvent));
                    } catch (Exception e) {
                        log.error("Failed to serialize user event", e);
                    }
                }
                case 2 -> {
                    // System health event
                    task.setEventType("SYSTEM_HEALTH");
                    task.setTaskName(generateSystemTaskName());
                    task.setTaskType("SYSTEM_MAINTENANCE");
                    metricsService.incrementSystemEvents();
                    
                    SystemHealthEvent systemEvent = new SystemHealthEvent();
                    systemEvent.setMetricType(systemMetrics[random.nextInt(systemMetrics.length)]);
                    systemEvent.setCurrentValue(50 + random.nextDouble() * 50);
                    systemEvent.setThreshold(80.0);
                    systemEvent.setSeverity(getSeverity(systemEvent.getCurrentValue()));
                    
                    try {
                        task.setEventPayload(objectMapper.writeValueAsString(systemEvent));
                    } catch (Exception e) {
                        log.error("Failed to serialize system event", e);
                    }
                }
            }
            
            // Set status and timestamps
            TaskStatus status = getRandomStatus();
            task.setStatus(status);
            task.setCreatedAt(createdAt);
            task.setTriggeredBy(UUID.randomUUID().toString());
            
            if (status == TaskStatus.COMPLETED || status == TaskStatus.FAILED) {
                task.setStartedAt(createdAt.plusSeconds(random.nextInt(5) + 1));
                int execTime = 100 + random.nextInt(3000);
                task.setExecutionTimeMs(execTime);
                task.setCompletedAt(task.getStartedAt().plusMillis(execTime));
                
                if (status == TaskStatus.COMPLETED) {
                    task.setResult(generateSuccessResult(task.getTaskName()));
                    metricsService.incrementTasksCompleted();
                } else {
                    task.setResult("Failed: " + generateFailureReason());
                    metricsService.incrementTasksFailed();
                }
            } else if (status == TaskStatus.RUNNING) {
                task.setStartedAt(createdAt.plusSeconds(random.nextInt(10) + 1));
            }
            
            tasks.add(task);
            metricsService.incrementTasksTriggered();
        }
        
        taskRepository.saveAll(tasks);
        log.info("Generated {} demo tasks", tasks.size());
        return tasks;
    }
    
    private String generateFileTaskName() {
        String[] names = {
            "Image Processing", "PDF Processing", "Video Encoding", 
            "File Compression", "Thumbnail Generation", "Document Indexing"
        };
        return names[random.nextInt(names.length)];
    }
    
    private String generateUserTaskName() {
        String[] names = {
            "Send Welcome Email", "Initialize User Profile", "Record User Analytics",
            "Sync Profile Data", "Create User Dashboard", "Send Notification"
        };
        return names[random.nextInt(names.length)];
    }
    
    private String generateSystemTaskName() {
        String[] names = {
            "CPU Cleanup", "Disk Cleanup", "Memory Cleanup", 
            "Cache Optimization", "Log Rotation", "Resource Monitoring"
        };
        return names[random.nextInt(names.length)];
    }
    
    private String getUserTaskType() {
        String[] types = {"EMAIL", "USER_MANAGEMENT", "ANALYTICS"};
        return types[random.nextInt(types.length)];
    }
    
    private String getRandomActionType() {
        String[] actions = {"REGISTRATION", "LOGIN", "PROFILE_UPDATE", "PASSWORD_CHANGE"};
        return actions[random.nextInt(actions.length)];
    }
    
    private String getFileType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".mp4")) {
            return "video/mp4";
        } else if (fileName.endsWith(".zip")) {
            return "application/zip";
        }
        return "application/octet-stream";
    }
    
    private String getSeverity(double value) {
        if (value >= 90) return "CRITICAL";
        if (value >= 75) return "HIGH";
        if (value >= 60) return "MEDIUM";
        return "LOW";
    }
    
    private TaskStatus getRandomStatus() {
        int rand = random.nextInt(100);
        if (rand < 60) return TaskStatus.COMPLETED;      // 60% completed
        if (rand < 75) return TaskStatus.PENDING;        // 15% pending
        if (rand < 90) return TaskStatus.RUNNING;        // 15% running
        if (rand < 95) return TaskStatus.FAILED;         // 5% failed
        return TaskStatus.DEAD_LETTER;                    // 5% dead letter
    }
    
    private String generateSuccessResult(String taskName) {
        String[] results = {
            "Successfully processed",
            "Completed without errors",
            "Operation finished successfully",
            "Task executed successfully",
            "All checks passed"
        };
        return results[random.nextInt(results.length)] + ": " + taskName.toLowerCase();
    }
    
    private String generateFailureReason() {
        String[] reasons = {
            "Timeout exceeded",
            "Resource unavailable",
            "Invalid input data",
            "Network connection lost",
            "Permission denied",
            "Out of memory"
        };
        return reasons[random.nextInt(reasons.length)];
    }
}

