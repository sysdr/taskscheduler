package com.scheduler.logging.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatusOrderByCreatedAtAsc(Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
    List<Task> findRecentTasks();
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = ?1")
    long countByStatus(Task.TaskStatus status);
    
    @Query("SELECT AVG(t.executionTimeMs) FROM Task t WHERE t.status = 'COMPLETED'")
    Double getAverageExecutionTime();
}
