package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    public Task createTask(String name, String description, int durationSeconds) {
        Task task = new Task(name, description, durationSeconds);
        Task savedTask = taskRepository.save(task);
        logger.info("Created task: {} with duration: {}s", name, durationSeconds);
        return savedTask;
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public void updateTaskStatus(Long taskId, Task.TaskStatus status) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(status);
            
            if (status == Task.TaskStatus.RUNNING) {
                task.setStartedAt(LocalDateTime.now());
            } else if (status == Task.TaskStatus.COMPLETED) {
                task.setCompletedAt(LocalDateTime.now());
                task.setProgressPercentage(100);
            }
            
            taskRepository.save(task);
            logger.info("Updated task {} status to {}", taskId, status);
        }
    }
    
    public void updateTaskProgress(Long taskId, int progressPercentage) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setProgressPercentage(progressPercentage);
            task.setLastCheckpoint(LocalDateTime.now());
            taskRepository.save(task);
        }
    }
    
    public void suspendTask(Long taskId, String reason) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(Task.TaskStatus.SUSPENDED);
            task.setErrorMessage("Suspended: " + reason);
            task.setLastCheckpoint(LocalDateTime.now());
            taskRepository.save(task);
            logger.info("Suspended task {} due to: {}", taskId, reason);
        }
    }
    
    public List<Task> getActiveOrPendingTasks() {
        return taskRepository.findActiveOrPendingTasks();
    }
    
    public List<Task> getTasksForRecovery() {
        return taskRepository.findTasksForRecovery();
    }
    
    public void markTaskForRetry(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(Task.TaskStatus.SCHEDULED_FOR_RETRY);
            taskRepository.save(task);
            logger.info("Marked task {} for retry", taskId);
        }
    }
}
