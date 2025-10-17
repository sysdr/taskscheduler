package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private AsyncTaskService asyncTaskService;
    
    public Task submitTask(Task task) {
        // Save task as pending
        task.setStatus(TaskStatus.PENDING);
        Task savedTask = taskRepository.save(task);
        
        // Submit for async processing
        CompletableFuture<String> future = submitTaskForAsyncProcessing(savedTask);
        savedTask.setFutureResult(future);
        
        // Update status to submitted
        savedTask.setStatus(TaskStatus.SUBMITTED);
        taskRepository.save(savedTask);
        
        logger.info("Submitted task '{}' of type '{}' for async processing", 
            savedTask.getName(), savedTask.getType());
        
        return savedTask;
    }
    
    private CompletableFuture<String> submitTaskForAsyncProcessing(Task task) {
        return switch (task.getType().toLowerCase()) {
            case "email" -> asyncTaskService.processEmailTask(task);
            case "report" -> asyncTaskService.processReportTask(task);
            case "data" -> asyncTaskService.processDataTask(task);
            default -> asyncTaskService.processDataTask(task); // Default fallback
        };
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAllOrderByCreatedAtDesc();
    }
    
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }
    
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
    
    public List<Task> getTasksByType(String type) {
        return taskRepository.findByTypeOrderByCreatedAtDesc(type);
    }
    
    public long getTaskCountByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
}
