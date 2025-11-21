package com.scheduler.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(Task.TaskStatus status);
    
    @Query("SELECT t.type, COUNT(t) FROM Task t GROUP BY t.type")
    List<Object[]> countByType();
    
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countByStatus();
}
