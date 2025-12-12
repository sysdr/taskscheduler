package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final AtomicInteger taskCounter = new AtomicInteger(0);
    
    @Value("${HOSTNAME:unknown}")
    private String podName;
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @Scheduled(fixedRate = 15000)
    public void processScheduledTasks() {
        logger.info("Pod {} scanning for tasks to execute", podName);
        List<Task> pendingTasks = taskRepository.findByStatus("PENDING");
        
        for (Task task : pendingTasks) {
            if (task.getScheduledTime().isBefore(LocalDateTime.now())) {
                executeTask(task);
            }
        }
    }
    
    private void executeTask(Task task) {
        logger.info("Pod {} executing task: {}", podName, task.getName());
        task.setStatus("COMPLETED");
        task.setExecutedTime(LocalDateTime.now());
        task.setPodName(podName);
        taskRepository.save(task);
        taskCounter.incrementAndGet();
    }
    
    public Task createTask(String name, String description, LocalDateTime scheduledTime) {
        Task task = new Task(name, description, scheduledTime);
        return taskRepository.save(task);
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public int getTasksProcessedCount() {
        return taskCounter.get();
    }
}
