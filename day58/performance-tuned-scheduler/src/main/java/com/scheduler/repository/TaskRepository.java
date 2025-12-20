package com.scheduler.repository;

import com.scheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(String status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(String status);
    
    @Query("SELECT AVG(t.executionTimeMs) FROM Task t WHERE t.status = 'COMPLETED'")
    Double averageExecutionTime();
}
