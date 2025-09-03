package com.scheduler.service;

import com.scheduler.dto.TaskDto;
import com.scheduler.entity.Task;
import com.scheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TaskManagementService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskManagementService.class);
    
    private final TaskRepository taskRepository;
    
    public TaskManagementService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @Transactional
    public TaskDto createTask(String name, String description, LocalDateTime scheduledTime) {
        Task task = new Task(name, description, scheduledTime);
        task = taskRepository.save(task);
        logger.info("Created new task: {}", task.getId());
        return TaskDto.from(task);
    }
    
    public Optional<TaskDto> getTask(Long id) {
        return taskRepository.findById(id).map(TaskDto::from);
    }
    
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskDto::from)
                .collect(Collectors.toList());
    }
    
    public List<TaskDto> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatusOrderByScheduledTimeAsc(status).stream()
                .map(TaskDto::from)
                .collect(Collectors.toList());
    }
    
    public List<TaskDto> getStuckTasks(int timeoutMinutes) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
        return taskRepository.findStuckRunningTasks(cutoffTime).stream()
                .map(TaskDto::from)
                .collect(Collectors.toList());
    }
    
    public TaskStatistics getStatistics() {
        long pending = taskRepository.countByStatus(Task.TaskStatus.PENDING);
        long running = taskRepository.countByStatus(Task.TaskStatus.RUNNING);
        long completed = taskRepository.countByStatus(Task.TaskStatus.COMPLETED);
        long failed = taskRepository.countByStatus(Task.TaskStatus.FAILED);
        long retrying = taskRepository.countByStatus(Task.TaskStatus.RETRYING);
        
        Optional<Double> avgProcessingTime = taskRepository.getAverageProcessingTimeInSeconds();
        
        return new TaskStatistics(pending, running, completed, failed, retrying, 
                                avgProcessingTime.orElse(0.0));
    }
    
    public record TaskStatistics(
        long pendingCount,
        long runningCount,
        long completedCount,
        long failedCount,
        long retryingCount,
        double averageProcessingTimeSeconds
    ) {}
}
