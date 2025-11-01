package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class TaskSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerService.class);
    private final Map<String, Task> scheduledTasks = new ConcurrentHashMap<>();
    
    @Autowired
    private TaskQueueService taskQueueService;

    public String scheduleTask(Task task) {
        if (task.getTaskId() == null) {
            task.setTaskId(UUID.randomUUID().toString());
        }
        task.setScheduledAt(LocalDateTime.now());
        scheduledTasks.put(task.getTaskId(), task);
        logger.info("Task scheduled: {} - {}", task.getTaskId(), task.getTaskName());
        return task.getTaskId();
    }

    @Scheduled(fixedDelay = 5000) // Check every 5 seconds
    public void processPendingTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasksToQueue = new ArrayList<>();
        
        scheduledTasks.values().stream()
            .filter(task -> task.getStatus() == TaskStatus.SCHEDULED)
            .filter(task -> task.getScheduledAt().isBefore(now) || task.getScheduledAt().isEqual(now))
            .forEach(tasksToQueue::add);
        
        for (Task task : tasksToQueue) {
            try {
                task.setStatus(TaskStatus.QUEUED);
                taskQueueService.sendTaskToQueue(task);
                logger.info("Task queued: {} - {}", task.getTaskId(), task.getTaskName());
            } catch (Exception e) {
                logger.error("Failed to queue task: {}", task.getTaskId(), e);
                task.setStatus(TaskStatus.FAILED);
            }
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(scheduledTasks.values());
    }

    public Task getTask(String taskId) {
        return scheduledTasks.get(taskId);
    }

    public void updateTaskStatus(String taskId, TaskStatus status) {
        Task task = scheduledTasks.get(taskId);
        if (task != null) {
            task.setStatus(status);
            if (status == TaskStatus.PROCESSING) {
                task.setExecutedAt(LocalDateTime.now());
            }
        }
    }

    public long getScheduledTasksCount() {
        return scheduledTasks.values().stream()
            .filter(task -> task.getStatus() == TaskStatus.SCHEDULED)
            .count();
    }

    public long getQueuedTasksCount() {
        return scheduledTasks.values().stream()
            .filter(task -> task.getStatus() == TaskStatus.QUEUED)
            .count();
    }

    public long getProcessingTasksCount() {
        return scheduledTasks.values().stream()
            .filter(task -> task.getStatus() == TaskStatus.PROCESSING)
            .count();
    }

    public long getCompletedTasksCount() {
        return scheduledTasks.values().stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
            .count();
    }

    public long getFailedTasksCount() {
        return scheduledTasks.values().stream()
            .filter(task -> task.getStatus() == TaskStatus.FAILED)
            .count();
    }
}
