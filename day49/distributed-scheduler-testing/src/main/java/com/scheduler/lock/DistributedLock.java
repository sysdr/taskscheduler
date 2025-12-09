package com.scheduler.lock;

import java.time.Duration;

public interface DistributedLock {
    boolean tryLock(String lockKey, String instanceId, Duration ttl);
    boolean unlock(String lockKey, String instanceId);
    boolean isLocked(String lockKey);
    String getLockOwner(String lockKey);
}
