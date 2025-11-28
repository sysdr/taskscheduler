package com.scheduler.repository;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    Long countByStatus(TaskStatus status);
    
    @Query("SELECT AVG(t.avgExecutionTimeMs) FROM Task t WHERE t.avgExecutionTimeMs IS NOT NULL")
    Double getAverageExecutionTime();
    
    @Query("SELECT SUM(t.executionCount) FROM Task t")
    Long getTotalExecutions();
}
