package com.scheduler.service;
import com.scheduler.model.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
@Service
public class TaskQueueService {
    private final RedisTemplate<String,Object> redis;
    public TaskQueueService(RedisTemplate<String,Object> redis) { this.redis = redis; }
    
    public void submit(Task task) {
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        redis.opsForList().rightPush("queue", task);
    }
    
    public Task poll() {
        Object obj = redis.opsForList().leftPop("queue", 1, TimeUnit.SECONDS);
        if (obj == null) return null;
        Task task = (Task) obj;
        task.setStatus(TaskStatus.PROCESSING);
        task.setStartedAt(LocalDateTime.now());
        redis.opsForList().rightPush("processing", task);
        return task;
    }
    
    public void complete(Task task) {
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        redis.opsForList().remove("processing", 1, task);
        redis.opsForList().rightPush("completed", task);
        redis.opsForList().trim("completed", -100, -1);
    }
    
    public int getQueueDepth() {
        Long size = redis.opsForList().size("queue");
        return size != null ? size.intValue() : 0;
    }
    
    public int getProcessingCount() {
        Long size = redis.opsForList().size("processing");
        return size != null ? size.intValue() : 0;
    }
}
