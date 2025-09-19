package com.taskscheduler.repository;

import com.taskscheduler.entity.ExecutionStatus;
import com.taskscheduler.entity.TaskExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    
    Optional<TaskExecution> findByExecutionId(String executionId);
    
    List<TaskExecution> findByStatus(ExecutionStatus status);
    
    List<TaskExecution> findByTaskNameAndStatus(String taskName, ExecutionStatus status);
    
    @Query("SELECT te FROM TaskExecution te WHERE te.startTime BETWEEN :startTime AND :endTime ORDER BY te.startTime DESC")
    List<TaskExecution> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.status = :status")
    Long countByStatus(@Param("status") ExecutionStatus status);
    
    @Query("SELECT AVG(te.durationMs) FROM TaskExecution te WHERE te.status = 'SUCCESS' AND te.durationMs IS NOT NULL")
    Double getAverageExecutionTime();
    
    @Query("SELECT te FROM TaskExecution te WHERE te.status = 'FAILED' ORDER BY te.startTime DESC")
    Page<TaskExecution> findFailedExecutions(Pageable pageable);
    
    @Query("SELECT te FROM TaskExecution te WHERE te.durationMs IS NOT NULL ORDER BY te.durationMs DESC")
    Page<TaskExecution> findLongestRunningTasks(Pageable pageable);
    
    @Query("SELECT te.taskName, COUNT(te) as count FROM TaskExecution te GROUP BY te.taskName ORDER BY count DESC")
    List<Object[]> getTaskExecutionCounts();
}
