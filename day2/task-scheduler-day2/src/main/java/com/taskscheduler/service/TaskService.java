package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong taskIdCounter = new AtomicLong(1);
    
    public TaskService() {
        // Initialize with some default tasks
        initializeDefaultTasks();
    }
    
    private void initializeDefaultTasks() {
        Task healthCheck = new Task("System Health Check", "Performs system health monitoring", 
                Task.TaskType.FIXED_RATE, "5000");
        healthCheck.setId("task-1");
        healthCheck.setLastRun(LocalDateTime.now().minusMinutes(2));
        healthCheck.setNextRun(LocalDateTime.now().plusMinutes(3));
        tasks.put(healthCheck.getId(), healthCheck);
        
        Task cleanup = new Task("System Cleanup", "Cleans up temporary files and logs", 
                Task.TaskType.FIXED_DELAY, "15000");
        cleanup.setId("task-2");
        cleanup.setLastRun(LocalDateTime.now().minusMinutes(1));
        cleanup.setNextRun(LocalDateTime.now().plusMinutes(14));
        tasks.put(cleanup.getId(), cleanup);
        
        Task reports = new Task("Daily Report Generation", "Generates daily system reports", 
                Task.TaskType.CRON, "0 * * * * *");
        reports.setId("task-3");
        reports.setLastRun(LocalDateTime.now().minusSeconds(30));
        reports.setNextRun(LocalDateTime.now().plusSeconds(30));
        tasks.put(reports.getId(), reports);
    }
    
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    
    public Task getTaskById(String id) {
        return tasks.get(id);
    }
    
    public Task createTask(Task task) {
        String id = "task-" + taskIdCounter.getAndIncrement();
        task.setId(id);
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus(Task.TaskStatus.ACTIVE);
        task.setActive(true);
        
        // Calculate next run based on task type
        calculateNextRun(task);
        
        tasks.put(id, task);
        return task;
    }
    
    public Task updateTask(String id, Task updatedTask) {
        Task existingTask = tasks.get(id);
        if (existingTask == null) {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
        
        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setType(updatedTask.getType());
        existingTask.setSchedule(updatedTask.getSchedule());
        existingTask.setActive(updatedTask.isActive());
        
        if (updatedTask.isActive()) {
            existingTask.setStatus(Task.TaskStatus.ACTIVE);
        } else {
            existingTask.setStatus(Task.TaskStatus.PAUSED);
        }
        
        calculateNextRun(existingTask);
        return existingTask;
    }
    
    public boolean deleteTask(String id) {
        Task removed = tasks.remove(id);
        return removed != null;
    }
    
    public Task pauseTask(String id) {
        Task task = tasks.get(id);
        if (task != null) {
            task.setActive(false);
            task.setStatus(Task.TaskStatus.PAUSED);
        }
        return task;
    }
    
    public Task resumeTask(String id) {
        Task task = tasks.get(id);
        if (task != null) {
            task.setActive(true);
            task.setStatus(Task.TaskStatus.ACTIVE);
            calculateNextRun(task);
        }
        return task;
    }
    
    public Task executeTask(String id) {
        Task task = tasks.get(id);
        if (task != null) {
            task.setLastRun(LocalDateTime.now());
            calculateNextRun(task);
        }
        return task;
    }
    
    private void calculateNextRun(Task task) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (task.getType()) {
            case FIXED_RATE:
                // For fixed rate, next run is based on the interval
                long interval = Long.parseLong(task.getSchedule());
                task.setNextRun(now.plus(Duration.ofMillis(interval)));
                break;
            case FIXED_DELAY:
                // For fixed delay, next run is after the delay from now
                long delay = Long.parseLong(task.getSchedule());
                task.setNextRun(now.plus(Duration.ofMillis(delay)));
                break;
            case CRON:
                // For cron, calculate next run based on cron expression
                // This is a simplified implementation
                task.setNextRun(now.plusMinutes(1));
                break;
        }
    }
    
    public List<Task> getActiveTasks() {
        return tasks.values().stream()
                .filter(Task::isActive)
                .toList();
    }
    
    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return tasks.values().stream()
                .filter(task -> task.getStatus() == status)
                .toList();
    }
    
    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTasks", tasks.size());
        stats.put("activeTasks", getActiveTasks().size());
        stats.put("pausedTasks", getTasksByStatus(Task.TaskStatus.PAUSED).size());
        stats.put("errorTasks", getTasksByStatus(Task.TaskStatus.ERROR).size());
        
        return stats;
    }
} 