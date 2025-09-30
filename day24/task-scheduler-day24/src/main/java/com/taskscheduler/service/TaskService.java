package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskPriority;
import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @Transactional
    public Task createTask(String name, String taskType, String description, TaskPriority priority, 
                          LocalDateTime scheduledTime, String taskData, Integer maxRetries) {
        Task task = new Task(name, taskType);
        task.setDescription(description);
        task.setPriority(priority);
        task.setScheduledTime(scheduledTime);
        task.setTaskData(taskData);
        if (maxRetries != null) {
            task.setMaxRetries(maxRetries);
        }
        
        Task savedTask = taskRepository.save(task);
        logger.info("Created task: {}", savedTask);
        return savedTask;
    }
    
    public List<Task> getTasksReadyForExecution() {
        return taskRepository.findReadyToExecute(TaskStatus.PENDING, LocalDateTime.now());
    }
    
    public List<Task> getRetriableTasks() {
        return taskRepository.findRetriableTasks(LocalDateTime.now());
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
    
    public long getTaskCountByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
    
    @Transactional
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setStatus(status);
            return taskRepository.save(task);
        }
        throw new RuntimeException("Task not found: " + taskId);
    }
    
    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
        logger.info("Deleted task: {}", taskId);
    }
}
