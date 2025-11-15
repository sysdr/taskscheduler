package com.taskscheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.model.*;
import com.taskscheduler.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventTaskService {
    
    private final TaskRepository taskRepository;
    private final TaskExecutorService taskExecutorService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MetricsService metricsService;
    private final ObjectMapper objectMapper;

    // File Event Tasks
    public void triggerImageProcessingTask(FileUploadEvent event) {
        Task task = createTask("Image Processing", "FILE_PROCESSING", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeImageProcessingTask(task);
    }

    public void triggerPdfProcessingTask(FileUploadEvent event) {
        Task task = createTask("PDF Processing", "FILE_PROCESSING", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executePdfProcessingTask(task);
    }

    public void triggerGenericFileProcessingTask(FileUploadEvent event) {
        Task task = createTask("Generic File Processing", "FILE_PROCESSING", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeGenericFileTask(task);
    }

    // User Event Tasks
    public void triggerWelcomeEmailTask(UserActionEvent event) {
        Task task = createTask("Send Welcome Email", "EMAIL", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeSendEmailTask(task);
    }

    public void triggerProfileInitializationTask(UserActionEvent event) {
        Task task = createTask("Initialize User Profile", "USER_MANAGEMENT", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeProfileInitTask(task);
    }

    public void triggerAnalyticsTask(UserActionEvent event) {
        Task task = createTask("Record User Analytics", "ANALYTICS", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeAnalyticsTask(task);
    }

    public void triggerLoginAnalyticsTask(UserActionEvent event) {
        Task task = createTask("Record Login Analytics", "ANALYTICS", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeAnalyticsTask(task);
    }

    public void triggerProfileSyncTask(UserActionEvent event) {
        Task task = createTask("Sync Profile Data", "USER_MANAGEMENT", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeProfileSyncTask(task);
    }

    // System Health Event Tasks
    public void triggerCpuCleanupTask(SystemHealthEvent event) {
        Task task = createTask("CPU Cleanup", "SYSTEM_MAINTENANCE", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeCpuCleanupTask(task);
    }

    public void triggerDiskCleanupTask(SystemHealthEvent event) {
        Task task = createTask("Disk Cleanup", "SYSTEM_MAINTENANCE", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeDiskCleanupTask(task);
    }

    public void triggerArchivalTask(SystemHealthEvent event) {
        Task task = createTask("Archive Old Files", "SYSTEM_MAINTENANCE", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeArchivalTask(task);
    }

    public void triggerMemoryCleanupTask(SystemHealthEvent event) {
        Task task = createTask("Memory Cleanup", "SYSTEM_MAINTENANCE", event);
        taskRepository.save(task);
        metricsService.incrementTasksTriggered();
        taskExecutorService.executeMemoryCleanupTask(task);
    }

    // Dead Letter Queue handling
    public void routeToDeadLetter(BaseEvent event, String reason) {
        try {
            log.warn("Routing event {} to dead letter queue. Reason: {}", 
                    event.getEventId(), reason);
            
            Task deadLetterTask = new Task();
            deadLetterTask.setTaskId(UUID.randomUUID().toString());
            deadLetterTask.setTaskName("Dead Letter - " + event.getEventType());
            deadLetterTask.setTaskType("DEAD_LETTER");
            deadLetterTask.setStatus(TaskStatus.DEAD_LETTER);
            deadLetterTask.setTriggeredBy(event.getEventId());
            deadLetterTask.setEventType(event.getEventType());
            deadLetterTask.setEventPayload(objectMapper.writeValueAsString(event));
            deadLetterTask.setResult("Failed: " + reason);
            
            taskRepository.save(deadLetterTask);
            metricsService.incrementDeadLetterEvents();
            
            kafkaTemplate.send("dead-letter-queue", event);
            
        } catch (Exception e) {
            log.error("Failed to route to dead letter queue", e);
        }
    }

    private Task createTask(String taskName, String taskType, BaseEvent event) {
        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskName(taskName);
        task.setTaskType(taskType);
        task.setStatus(TaskStatus.PENDING);
        task.setTriggeredBy(event.getEventId());
        task.setEventType(event.getEventType());
        
        try {
            task.setEventPayload(objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            log.error("Failed to serialize event payload", e);
        }
        
        return task;
    }
}
