package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String TASK_CACHE_PREFIX = "task:";
    private static final String STATS_KEY = "task:stats";
    
    public Task createTask(Task task) {
        task.setNextRunTime(LocalDateTime.now().plusMinutes(5));
        Task saved = taskRepository.save(task);
        cacheTask(saved);
        updateStats();
        return saved;
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAllOrderedByPriority();
    }
    
    public Optional<Task> getTaskById(Long id) {
        String cacheKey = TASK_CACHE_PREFIX + id;
        Task cached = (Task) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Optional.of(cached);
        }
        
        Optional<Task> task = taskRepository.findById(id);
        task.ifPresent(this::cacheTask);
        return task;
    }
    
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setName(taskDetails.getName());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setCronExpression(taskDetails.getCronExpression());
        task.setPriority(taskDetails.getPriority());
        
        Task updated = taskRepository.save(task);
        cacheTask(updated);
        updateStats();
        return updated;
    }
    
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
        redisTemplate.delete(TASK_CACHE_PREFIX + id);
        updateStats();
    }
    
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", taskRepository.count());
        stats.put("pending", taskRepository.countByStatus("PENDING"));
        stats.put("running", taskRepository.countByStatus("RUNNING"));
        stats.put("completed", taskRepository.countByStatus("COMPLETED"));
        stats.put("failed", taskRepository.countByStatus("FAILED"));
        return stats;
    }
    
    @Scheduled(fixedRate = 30000)
    public void processScheduledTasks() {
        List<Task> pendingTasks = taskRepository.findByStatus("PENDING");
        LocalDateTime now = LocalDateTime.now();
        
        for (Task task : pendingTasks) {
            if (task.getNextRunTime() != null && task.getNextRunTime().isBefore(now)) {
                executeTask(task);
            }
        }
    }
    
    private void executeTask(Task task) {
        task.setStatus("RUNNING");
        taskRepository.save(task);
        
        try {
            Thread.sleep(2000);
            task.setStatus("COMPLETED");
            task.setNextRunTime(LocalDateTime.now().plusMinutes(10));
        } catch (Exception e) {
            task.setStatus("FAILED");
        }
        
        taskRepository.save(task);
        cacheTask(task);
        updateStats();
    }
    
    private void cacheTask(Task task) {
        String cacheKey = TASK_CACHE_PREFIX + task.getId();
        redisTemplate.opsForValue().set(cacheKey, task, 10, TimeUnit.MINUTES);
    }
    
    private void updateStats() {
        Map<String, Long> stats = getStatistics();
        redisTemplate.opsForHash().putAll(STATS_KEY, 
            Map.of(
                "total", stats.get("total").toString(),
                "pending", stats.get("pending").toString(),
                "running", stats.get("running").toString(),
                "completed", stats.get("completed").toString(),
                "failed", stats.get("failed").toString()
            )
        );
    }
}
