package com.taskscheduler.batch.service;

import com.taskscheduler.batch.model.Task;
import com.taskscheduler.batch.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final BatchAccumulator batchAccumulator;
    
    @Transactional
    public Task createTask(String taskType, String payload) {
        Task task = Task.builder()
                .taskId(UUID.randomUUID().toString())
                .taskType(taskType)
                .payload(payload)
                .status(Task.TaskStatus.PENDING)
                .build();
        
        task = taskRepository.save(task);
        log.debug("Created task: {}", task.getTaskId());
        
        return task;
    }
    
    @Scheduled(fixedDelay = 2000)
    @Transactional(readOnly = true)
    public void loadPendingTasks() {
        List<Task> pendingTasks = taskRepository.findByStatusOrderByCreatedAtAsc(Task.TaskStatus.PENDING);
        
        if (!pendingTasks.isEmpty()) {
            log.info("Loading {} pending tasks into batch accumulator", pendingTasks.size());
            
            for (Task task : pendingTasks) {
                batchAccumulator.addTask(task);
            }
        }
    }
    
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void resetStuckTasks() {
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(5);
        int resetCount = taskRepository.resetStuckTasks(
                Task.TaskStatus.PROCESSING, 
                Task.TaskStatus.RETRY, 
                timeout
        );
        
        if (resetCount > 0) {
            log.warn("Reset {} stuck tasks", resetCount);
        }
    }
    
    public TaskStats getStats() {
        long pending = taskRepository.countByStatus(Task.TaskStatus.PENDING);
        long processing = taskRepository.countByStatus(Task.TaskStatus.PROCESSING);
        long completed = taskRepository.countByStatus(Task.TaskStatus.COMPLETED);
        long failed = taskRepository.countByStatus(Task.TaskStatus.FAILED);
        long retry = taskRepository.countByStatus(Task.TaskStatus.RETRY);
        int queueSize = batchAccumulator.getQueueSize();
        
        return new TaskStats(pending, processing, completed, failed, retry, queueSize);
    }
    
    public record TaskStats(long pending, long processing, long completed, long failed, long retry, int queueSize) {}
}
