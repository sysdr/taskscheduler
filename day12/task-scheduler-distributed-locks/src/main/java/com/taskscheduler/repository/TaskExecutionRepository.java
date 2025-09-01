package com.taskscheduler.repository;

import com.taskscheduler.entity.TaskExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    
    List<TaskExecution> findByTaskKey(String taskKey);
    
    List<TaskExecution> findByInstanceId(String instanceId);
    
    List<TaskExecution> findByStatus(TaskExecution.ExecutionStatus status);
    
    Optional<TaskExecution> findByTaskKeyAndStatus(String taskKey, TaskExecution.ExecutionStatus status);
    
    @Query("SELECT te FROM TaskExecution te WHERE te.startedAt >= :startTime AND te.startedAt <= :endTime")
    List<TaskExecution> findByStartedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.status = :status")
    long countByStatus(@Param("status") TaskExecution.ExecutionStatus status);
    
    @Query("SELECT AVG(te.durationMs) FROM TaskExecution te WHERE te.status = 'COMPLETED' AND te.taskKey = :taskKey")
    Double getAverageDurationForTask(@Param("taskKey") String taskKey);
}
