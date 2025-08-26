package com.ultrascale.scheduler.repository;

import com.ultrascale.scheduler.model.TaskResult;
import com.ultrascale.scheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskResultRepository extends JpaRepository<TaskResult, Long> {
    
    List<TaskResult> findByTaskDefinitionId(Long taskDefinitionId);
    
    List<TaskResult> findByStatus(TaskStatus status);
    
    List<TaskResult> findByStartedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM TaskResult t WHERE t.taskDefinition.id = :taskId ORDER BY t.startedAt DESC")
    List<TaskResult> findLatestResultsByTaskId(@Param("taskId") Long taskId);
    
    @Query("SELECT COUNT(t) FROM TaskResult t WHERE t.status = :status AND t.startedAt >= :since")
    Long countByStatusSince(@Param("status") TaskStatus status, @Param("since") LocalDateTime since);
}
