package com.taskscheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Collections;

@Service
public class RedisLockService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String LOCK_PREFIX = "task_lock:";
    private static final Duration DEFAULT_LOCK_TIMEOUT = Duration.ofSeconds(30);
    
    // Lua script for atomic lock release
    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "return redis.call('del', KEYS[1]) " +
        "else return 0 end";
    
    public boolean acquireLock(String taskId, String executorId) {
        return acquireLock(taskId, executorId, DEFAULT_LOCK_TIMEOUT);
    }
    
    public boolean acquireLock(String taskId, String executorId, Duration timeout) {
        String lockKey = LOCK_PREFIX + taskId;
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, executorId, timeout);
        return Boolean.TRUE.equals(result);
    }
    
    public boolean refreshLock(String taskId, String executorId, Duration timeout) {
        String lockKey = LOCK_PREFIX + taskId;
        String currentOwner = redisTemplate.opsForValue().get(lockKey);
        
        if (executorId.equals(currentOwner)) {
            redisTemplate.expire(lockKey, timeout);
            return true;
        }
        return false;
    }
    
    public boolean releaseLock(String taskId, String executorId) {
        String lockKey = LOCK_PREFIX + taskId;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = redisTemplate.execute(script, Collections.singletonList(lockKey), executorId);
        return result != null && result == 1L;
    }
    
    public boolean isLocked(String taskId) {
        String lockKey = LOCK_PREFIX + taskId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }
    
    public String getLockOwner(String taskId) {
        String lockKey = LOCK_PREFIX + taskId;
        return redisTemplate.opsForValue().get(lockKey);
    }
}
