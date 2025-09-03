package com.scheduler.repository;

import com.scheduler.entity.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find available tasks for processing
    @Query("SELECT t FROM Task t WHERE " +
           "(t.status = 'PENDING' OR t.status = 'RETRYING') " +
           "AND t.scheduledTime <= :now " +
           "AND t.retryCount < t.maxRetries " +
           "ORDER BY t.scheduledTime ASC")
    List<Task> findAvailableTasksForProcessing(@Param("now") LocalDateTime now, Pageable pageable);
    
    // Optimistic lock-aware update for claiming a task
    @Modifying
    @Query("UPDATE Task t SET " +
           "t.status = 'RUNNING', " +
           "t.processorId = :processorId, " +
           "t.startedAt = :startedAt, " +
           "t.updatedAt = :updatedAt, " +
           "t.version = t.version + 1 " +
           "WHERE t.id = :taskId AND t.version = :expectedVersion " +
           "AND (t.status = 'PENDING' OR t.status = 'RETRYING')")
    int claimTaskOptimistically(@Param("taskId") Long taskId,
                               @Param("expectedVersion") Long expectedVersion,
                               @Param("processorId") String processorId,
                               @Param("startedAt") LocalDateTime startedAt,
                               @Param("updatedAt") LocalDateTime updatedAt);
    
    // Update task to completed status
    @Modifying
    @Query("UPDATE Task t SET " +
           "t.status = 'COMPLETED', " +
           "t.completedAt = :completedAt, " +
           "t.updatedAt = :updatedAt, " +
           "t.version = t.version + 1 " +
           "WHERE t.id = :taskId AND t.version = :expectedVersion " +
           "AND t.status = 'RUNNING' AND t.processorId = :processorId")
    int markTaskCompleted(@Param("taskId") Long taskId,
                         @Param("expectedVersion") Long expectedVersion,
                         @Param("processorId") String processorId,
                         @Param("completedAt") LocalDateTime completedAt,
                         @Param("updatedAt") LocalDateTime updatedAt);
    
    // Update task to failed status
    @Modifying
    @Query("UPDATE Task t SET " +
           "t.status = :status, " +
           "t.errorMessage = :errorMessage, " +
           "t.retryCount = t.retryCount + :incrementRetry, " +
           "t.updatedAt = :updatedAt, " +
           "t.version = t.version + 1 " +
           "WHERE t.id = :taskId AND t.version = :expectedVersion " +
           "AND t.status = 'RUNNING' AND t.processorId = :processorId")
    int markTaskFailed(@Param("taskId") Long taskId,
                      @Param("expectedVersion") Long expectedVersion,
                      @Param("processorId") String processorId,
                      @Param("status") Task.TaskStatus status,
                      @Param("errorMessage") String errorMessage,
                      @Param("incrementRetry") int incrementRetry,
                      @Param("updatedAt") LocalDateTime updatedAt);
    
    // Find tasks by status
    List<Task> findByStatusOrderByScheduledTimeAsc(Task.TaskStatus status);
    
    // Find running tasks that might be stuck
    @Query("SELECT t FROM Task t WHERE " +
           "t.status = 'RUNNING' " +
           "AND t.startedAt < :cutoffTime")
    List<Task> findStuckRunningTasks(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // Count tasks by status
    long countByStatus(Task.TaskStatus status);
    
    // Statistics
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countTasksByStatus(@Param("status") Task.TaskStatus status);
    
    @Query("SELECT AVG(CAST((t.completedAt - t.startedAt) AS double)) " +
           "FROM Task t WHERE t.status = 'COMPLETED' AND t.completedAt IS NOT NULL AND t.startedAt IS NOT NULL")
    Optional<Double> getAverageProcessingTimeInSeconds();
}
