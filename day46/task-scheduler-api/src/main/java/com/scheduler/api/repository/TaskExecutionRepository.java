package com.scheduler.api.repository;

import com.scheduler.api.model.ExecutionStatus;
import com.scheduler.api.model.TaskExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    Page<TaskExecution> findByTaskId(Long taskId, Pageable pageable);
    Page<TaskExecution> findByStatus(ExecutionStatus status, Pageable pageable);
    Page<TaskExecution> findByStartTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    List<TaskExecution> findByTaskIdAndStartTimeAfter(Long taskId, LocalDateTime startTime);
    
    @Query("SELECT COUNT(e) FROM TaskExecution e WHERE e.taskId = :taskId AND e.status = :status")
    Long countByTaskIdAndStatus(Long taskId, ExecutionStatus status);
    
    @Query("SELECT AVG(e.durationMs) FROM TaskExecution e WHERE e.taskId = :taskId AND e.durationMs IS NOT NULL")
    Double getAverageDurationByTaskId(Long taskId);
}
