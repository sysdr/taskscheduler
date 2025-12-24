package com.taskscheduler.repository;

import com.taskscheduler.domain.TaskExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    List<TaskExecution> findByTaskIdOrderByStartTimeDesc(Long taskId);
    
    @Query("SELECT e FROM TaskExecution e WHERE e.startTime >= :since ORDER BY e.startTime DESC")
    List<TaskExecution> findRecentExecutions(LocalDateTime since);
    
    @Query("SELECT AVG(e.durationMs) FROM TaskExecution e WHERE e.taskId = :taskId AND e.status = 'SUCCESS'")
    Double getAverageDuration(Long taskId);
}
