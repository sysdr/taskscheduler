package com.scheduler.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDistributedLock implements DistributedLock {
    private static final Logger log = LoggerFactory.getLogger(RedisDistributedLock.class);
    private final RedisTemplate<String, String> redisTemplate;

    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    public RedisDistributedLock(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryLock(String lockKey, String instanceId, Duration ttl) {
        try {
            Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, instanceId, ttl.toMillis(), TimeUnit.MILLISECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error acquiring lock", e);
            return false;
        }
    }

    @Override
    public boolean unlock(String lockKey, String instanceId) {
        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(UNLOCK_SCRIPT);
            script.setResultType(Long.class);
            Long result = redisTemplate.execute(script, Collections.singletonList(lockKey), instanceId);
            return result != null && result == 1L;
        } catch (Exception e) {
            log.error("Error releasing lock", e);
            return false;
        }
    }

    @Override
    public boolean isLocked(String lockKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    @Override
    public String getLockOwner(String lockKey) {
        return redisTemplate.opsForValue().get(lockKey);
    }
}
