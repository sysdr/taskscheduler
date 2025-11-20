package com.scheduler.metrics.repository;

import com.scheduler.metrics.model.Task;
import com.scheduler.metrics.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(TaskStatus status);
    
    long countByStatus(TaskStatus status);
    
    long countByType(String type);
    
    @Query("SELECT t.type, COUNT(t) FROM Task t GROUP BY t.type")
    List<Object[]> countByTypeGrouped();
    
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countByStatusGrouped();
    
    @Query("SELECT AVG(t.executionTimeMs) FROM Task t WHERE t.status = 'COMPLETED'")
    Double getAverageExecutionTime();
}
