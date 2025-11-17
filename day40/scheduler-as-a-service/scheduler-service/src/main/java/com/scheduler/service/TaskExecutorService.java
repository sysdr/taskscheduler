package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskExecutorService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskService taskService;
    
    @Scheduled(fixedDelay = 5000)
    public void pollAndExecuteTasks() {
        List<Task> pendingTasks = taskRepository.findByStatusAndScheduledForBefore(
            TaskStatus.SCHEDULED, LocalDateTime.now()
        );
        
        for (Task task : pendingTasks) {
            executeTaskAsync(task);
        }
        
        // Also process PENDING tasks
        List<Task> immediateTasks = taskRepository.findByStatusAndScheduledForBefore(
            TaskStatus.PENDING, LocalDateTime.now()
        );
        
        for (Task task : immediateTasks) {
            executeTaskAsync(task);
        }
    }
    
    @Async
    public void executeTaskAsync(Task task) {
        taskService.executeTask(task);
    }
}
