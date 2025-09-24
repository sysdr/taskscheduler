package com.taskscheduler.repository;

import com.taskscheduler.entity.TaskExecution;
import com.taskscheduler.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    
    List<TaskExecution> findByStatus(TaskStatus status);
    
    List<TaskExecution> findByTaskName(String taskName);
    
    List<TaskExecution> findByTaskNameAndStatus(String taskName, TaskStatus status);
    
    @Query("SELECT te FROM TaskExecution te WHERE te.status = :status AND te.createdAt <= :cutoffTime")
    List<TaskExecution> findStaleTasksByStatus(@Param("status") TaskStatus status, 
                                              @Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.status = :status")
    Long countByStatus(@Param("status") TaskStatus status);
    
    @Query("SELECT te.status, COUNT(te) FROM TaskExecution te GROUP BY te.status")
    List<Object[]> getStatusCounts();
    
    @Query("SELECT AVG(te.durationMs) FROM TaskExecution te WHERE te.status = 'SUCCEEDED' AND te.durationMs IS NOT NULL")
    Optional<Double> getAverageExecutionTime();
    
    @Query("SELECT te FROM TaskExecution te WHERE te.status IN :statuses ORDER BY te.createdAt DESC")
    List<TaskExecution> findByStatusInOrderByCreatedAtDesc(@Param("statuses") List<TaskStatus> statuses);
    
    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.status = 'FAILED' AND te.createdAt >= :since")
    Long countFailedTasksSince(@Param("since") LocalDateTime since);
}
