package com.taskscheduler.repository;

import com.taskscheduler.entity.TaskLock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskLockRepository extends JpaRepository<TaskLock, Long> {
    
    /**
     * Find lock by key with pessimistic write lock (SELECT FOR UPDATE)
     * This ensures exclusive access to the lock record
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tl FROM TaskLock tl WHERE tl.lockKey = :lockKey")
    Optional<TaskLock> findByLockKeyForUpdate(@Param("lockKey") String lockKey);
    
    /**
     * Find lock by key without locking (for read-only operations)
     */
    Optional<TaskLock> findByLockKey(String lockKey);
    
    /**
     * Find all locks owned by a specific instance
     */
    List<TaskLock> findByOwnerInstance(String ownerInstance);
    
    /**
     * Find all expired locks
     */
    @Query("SELECT tl FROM TaskLock tl WHERE tl.expiresAt < :currentTime")
    List<TaskLock> findExpiredLocks(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Delete lock by key and owner (for safe cleanup)
     */
    @Modifying
    @Query("DELETE FROM TaskLock tl WHERE tl.lockKey = :lockKey AND tl.ownerInstance = :ownerInstance")
    int deleteByLockKeyAndOwner(@Param("lockKey") String lockKey, @Param("ownerInstance") String ownerInstance);
    
    /**
     * Delete all expired locks
     */
    @Modifying
    @Query("DELETE FROM TaskLock tl WHERE tl.expiresAt < :currentTime")
    int deleteExpiredLocks(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Count active locks (non-expired)
     */
    @Query("SELECT COUNT(tl) FROM TaskLock tl WHERE tl.expiresAt >= :currentTime")
    long countActiveLocks(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find locks by task type
     */
    List<TaskLock> findByTaskType(String taskType);
    
    /**
     * Check if lock exists and is not expired
     */
    @Query("SELECT CASE WHEN COUNT(tl) > 0 THEN true ELSE false END FROM TaskLock tl " +
           "WHERE tl.lockKey = :lockKey AND tl.expiresAt >= :currentTime")
    boolean existsActiveLock(@Param("lockKey") String lockKey, @Param("currentTime") LocalDateTime currentTime);
}
