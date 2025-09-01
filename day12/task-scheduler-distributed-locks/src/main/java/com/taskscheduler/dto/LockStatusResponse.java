package com.taskscheduler.dto;

import com.taskscheduler.service.LockInfo;
import com.taskscheduler.service.LockStatistics;

import java.time.LocalDateTime;
import java.util.List;

public record LockStatusResponse(
    LockStatistics statistics,
    List<LockDetails> activeLocks
) {
    
    public record LockDetails(
        String lockKey,
        String ownerInstance,
        LocalDateTime acquiredAt,
        LocalDateTime expiresAt,
        String taskType,
        long holdTimeMs,
        boolean isExpired
    ) {
        public static LockDetails from(LockInfo lockInfo) {
            long holdTime = lockInfo.acquiredAt() != null ? 
                java.time.Duration.between(lockInfo.acquiredAt(), LocalDateTime.now()).toMillis() : 0;
                
            return new LockDetails(
                lockInfo.lockKey(),
                lockInfo.ownerInstance(),
                lockInfo.acquiredAt(),
                lockInfo.expiresAt(),
                lockInfo.taskType(),
                holdTime,
                lockInfo.isExpired()
            );
        }
    }
}
