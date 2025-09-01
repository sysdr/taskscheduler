package com.taskscheduler.service;

import java.time.LocalDateTime;

public record LockInfo(
    String lockKey,
    String ownerInstance,
    LocalDateTime acquiredAt,
    LocalDateTime expiresAt,
    String taskType,
    String description,
    boolean isExpired
) {
    public static LockInfo notFound(String lockKey) {
        return new LockInfo(lockKey, null, null, null, null, "Lock not found", true);
    }
}
