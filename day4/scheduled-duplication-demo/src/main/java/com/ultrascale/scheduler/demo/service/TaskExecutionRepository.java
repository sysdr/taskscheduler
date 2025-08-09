package com.ultrascale.scheduler.demo.service;

import com.ultrascale.scheduler.demo.model.TaskExecutionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecutionRecord, Long> {
    
    List<TaskExecutionRecord> findByTaskNameOrderByExecutionTimeDesc(String taskName);
    
    @Query("SELECT t FROM TaskExecutionRecord t WHERE t.executionTime >= ?1 ORDER BY t.executionTime DESC")
    List<TaskExecutionRecord> findRecentExecutions(LocalDateTime since);
    
    @Query("SELECT COUNT(t) FROM TaskExecutionRecord t WHERE t.taskName = ?1 AND t.executionTime >= ?2")
    long countExecutionsSince(String taskName, LocalDateTime since);
}
