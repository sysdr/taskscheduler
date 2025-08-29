package com.example.distributedlock.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskExecutionLogRepository extends JpaRepository<TaskExecutionLog, Long> {
    
    List<TaskExecutionLog> findByTaskNameOrderByExecutionTimeDesc(String taskName);
    
    @Query("SELECT t FROM TaskExecutionLog t WHERE t.taskName = ?1 AND t.executionTime >= ?2")
    List<TaskExecutionLog> findRecentExecutions(String taskName, LocalDateTime since);
    
    @Query("SELECT COUNT(t) FROM TaskExecutionLog t WHERE t.taskName = ?1 AND t.executionTime >= ?2")
    long countRecentExecutions(String taskName, LocalDateTime since);
}
