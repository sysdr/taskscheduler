package com.scheduler.manager;

import com.scheduler.config.RedlockConfig.RedisInstance;
import com.scheduler.model.RedlockResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RedlockManager {
    
    private static final Logger log = LoggerFactory.getLogger(RedlockManager.class);
    private final List<RedisTemplate<String, String>> redisTemplates;
    private final int retryCount;
    private final long retryDelay;
    private final double clockDriftFactor;
    
    private final DefaultRedisScript<String> lockScript;
    private final DefaultRedisScript<Long> unlockScript;

    public RedlockManager(List<RedisInstance> instances, int retryCount, long retryDelay, double clockDriftFactor) {
        this.retryCount = retryCount;
        this.retryDelay = retryDelay;
        this.clockDriftFactor = clockDriftFactor;
        this.redisTemplates = initializeRedisTemplates(instances);
        
        // Lua script for atomic lock acquisition
        this.lockScript = new DefaultRedisScript<>(
            "if redis.call('set', KEYS[1], ARGV[1], 'nx', 'px', ARGV[2]) then return 'OK' else return nil end",
            String.class
        );
        
        // Lua script for atomic lock release
        this.unlockScript = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class
        );
    }

    private List<RedisTemplate<String, String>> initializeRedisTemplates(List<RedisInstance> instances) {
        List<RedisTemplate<String, String>> templates = new ArrayList<>();
        
        for (RedisInstance instance : instances) {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(instance.getHost());
            config.setPort(instance.getPort());
            
            LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
            factory.afterPropertiesSet();
            
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(factory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            template.afterPropertiesSet();
            
            templates.add(template);
        }
        
        return templates;
    }

    public RedlockResult lock(String resource, long ttlMillis) {
        String lockValue = UUID.randomUUID().toString();
        
        for (int i = 0; i < retryCount; i++) {
            long startTime = System.currentTimeMillis();
            
            RedlockResult result = attemptLock(resource, lockValue, ttlMillis);
            
            if (result.isSuccess()) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long validityTime = ttlMillis - elapsedTime - (long) (ttlMillis * clockDriftFactor);
                
                if (validityTime > 0) {
                    log.info("Lock acquired successfully for resource: {} with value: {} validity: {}ms", 
                            resource, lockValue, validityTime);
                    return new RedlockResult(true, lockValue, validityTime, result.getAcquiredInstances());
                } else {
                    // Lock expired during acquisition, release it
                    unlock(resource, lockValue);
                }
            }
            
            // Wait before retry
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(retryDelay));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        log.warn("Failed to acquire lock for resource: {} after {} retries", resource, retryCount);
        return new RedlockResult(false, null, 0, 0);
    }

    private RedlockResult attemptLock(String resource, String lockValue, long ttlMillis) {
        int successCount = 0;
        List<Boolean> results = new ArrayList<>(redisTemplates.size());
        
        for (RedisTemplate<String, String> template : redisTemplates) {
            try {
                String result = template.execute(lockScript, 
                    Collections.singletonList(resource), 
                    lockValue, 
                    String.valueOf(ttlMillis)
                );
                
                boolean acquired = "OK".equals(result);
                results.add(acquired);
                if (acquired) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("Failed to acquire lock on Redis instance: {}", e.getMessage());
                results.add(false);
            }
        }
        
        boolean success = successCount >= (redisTemplates.size() / 2 + 1);
        
        if (!success) {
            // Release any locks that were acquired
            releaseLocks(resource, lockValue, results);
        }
        
        return new RedlockResult(success, lockValue, 0, successCount);
    }

    public boolean unlock(String resource, String lockValue) {
        int successCount = 0;
        
        for (RedisTemplate<String, String> template : redisTemplates) {
            try {
                Long result = template.execute(unlockScript, 
                    Collections.singletonList(resource), 
                    lockValue
                );
                
                if (result != null && result == 1) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("Failed to release lock on Redis instance: {}", e.getMessage());
            }
        }
        
        log.info("Released lock for resource: {} on {} instances", resource, successCount);
        return successCount > 0;
    }

    private void releaseLocks(String resource, String lockValue, List<Boolean> acquisitionResults) {
        for (int i = 0; i < redisTemplates.size(); i++) {
            if (acquisitionResults.get(i)) {
                try {
                    redisTemplates.get(i).execute(unlockScript, 
                        Collections.singletonList(resource), 
                        lockValue
                    );
                } catch (Exception e) {
                    log.warn("Failed to release lock during cleanup: {}", e.getMessage());
                }
            }
        }
    }
}
