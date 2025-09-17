package com.taskscheduler.repository;

import com.taskscheduler.model.ExecutionStatus;
import com.taskscheduler.model.TaskExecution;
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
    
    List<TaskExecution> findByTaskNameAndStatusOrderByCreatedAtDesc(String taskName, ExecutionStatus status);
    
    @Query("SELECT te FROM TaskExecution te WHERE te.taskName = :taskName AND te.createdAt >= :since ORDER BY te.createdAt DESC")
    List<TaskExecution> findRecentExecutions(@Param("taskName") String taskName, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.status = :status")
    long countByStatus(@Param("status") ExecutionStatus status);
    
    Page<TaskExecution> findByTaskNameOrderByCreatedAtDesc(String taskName, Pageable pageable);
    
    @Query("SELECT te.taskName, COUNT(te) as count FROM TaskExecution te WHERE te.createdAt >= :since GROUP BY te.taskName")
    List<Object[]> getExecutionStatsSince(@Param("since") LocalDateTime since);
}
