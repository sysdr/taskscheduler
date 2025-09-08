package com.scheduler.service;

import com.scheduler.manager.RedlockManager;
import com.scheduler.model.RedlockResult;
import com.scheduler.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class TaskSchedulerService {
    
    private static final Logger log = LoggerFactory.getLogger(TaskSchedulerService.class);
    private static final long LOCK_TTL = 30000; // 30 seconds
    private final RedlockManager redlockManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();
    private final String instanceId = UUID.randomUUID().toString().substring(0, 8);

    @Autowired
    public TaskSchedulerService(RedlockManager redlockManager, RedisTemplate<String, Object> redisTemplate) {
        this.redlockManager = redlockManager;
        this.redisTemplate = redisTemplate;
        initializeSampleTasks();
    }

    private void initializeSampleTasks() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task(
                "task-" + i,
                "Sample Task " + i,
                "This is a sample task for testing Redlock algorithm",
                LocalDateTime.now().plusSeconds(ThreadLocalRandom.current().nextInt(10, 60))
            );
            tasks.put(task.getId(), task);
            
            // Store in Redis for persistence
            redisTemplate.opsForHash().put("tasks", task.getId(), task);
        }
    }

    @Scheduled(fixedRate = 5000) // Check every 5 seconds
    public void processPendingTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Task> pendingTasks = tasks.values().stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.PENDING)
                .filter(task -> task.getScheduledTime().isBefore(now))
                .collect(Collectors.toList());

        for (Task task : pendingTasks) {
            executeTaskWithRedlock(task);
        }
    }

    private void executeTaskWithRedlock(Task task) {
        String lockKey = "task_lock:" + task.getId();
        
        RedlockResult lockResult = redlockManager.lock(lockKey, LOCK_TTL);
        
        if (lockResult.isSuccess()) {
            try {
                log.info("Instance {} acquired lock for task: {}", instanceId, task.getId());
                
                // Simulate task execution
                task.setStatus(Task.TaskStatus.RUNNING);
                task.setExecutedBy(instanceId);
                task.setExecutedTime(LocalDateTime.now());
                task.setLockValue(lockResult.getLockValue());
                
                // Update in Redis
                redisTemplate.opsForHash().put("tasks", task.getId(), task);
                
                // Simulate work
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3000));
                
                task.setStatus(Task.TaskStatus.COMPLETED);
                redisTemplate.opsForHash().put("tasks", task.getId(), task);
                
                log.info("Instance {} completed task: {}", instanceId, task.getId());
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                task.setStatus(Task.TaskStatus.FAILED);
                log.error("Task execution interrupted: {}", task.getId());
            } finally {
                // Always release the lock
                redlockManager.unlock(lockKey, lockResult.getLockValue());
            }
        } else {
            log.debug("Instance {} failed to acquire lock for task: {}", instanceId, task.getId());
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task addTask(String name, String description, LocalDateTime scheduledTime) {
        Task task = new Task(
            UUID.randomUUID().toString(),
            name,
            description,
            scheduledTime
        );
        
        tasks.put(task.getId(), task);
        redisTemplate.opsForHash().put("tasks", task.getId(), task);
        
        log.info("Added new task: {}", task.getId());
        return task;
    }

    public String getInstanceId() {
        return instanceId;
    }
}
