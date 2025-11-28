package com.scheduler.repository;

import com.scheduler.model.TaskExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    
    List<TaskExecution> findByTaskIdOrderByStartedAtDesc(Long taskId);
    
    @Query("SELECT te FROM TaskExecution te WHERE te.startedAt >= :since ORDER BY te.startedAt DESC")
    List<TaskExecution> findRecentExecutions(LocalDateTime since);
}
