package com.scheduler.repository;

import com.scheduler.model.TaskExecution;
import com.scheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    
    List<TaskExecution> findByTaskNameOrderByStartedAtDesc(String taskName);
    
    List<TaskExecution> findByInstanceIdAndStatus(String instanceId, TaskStatus status);
    
    @Query("SELECT te FROM TaskExecution te WHERE te.startedAt >= :since ORDER BY te.startedAt DESC")
    List<TaskExecution> findRecentExecutions(LocalDateTime since);
    
    @Query("SELECT AVG(te.executionTimeMs) FROM TaskExecution te WHERE te.taskName = :taskName AND te.status = 'COMPLETED'")
    Double getAverageExecutionTime(String taskName);
    
    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.status = 'RUNNING'")
    long countRunningExecutions();
}
