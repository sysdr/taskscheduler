package com.taskscheduler.repository;

import com.taskscheduler.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatusAndEnabledTrue(Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.enabled = true AND t.status = 'SCHEDULED' " +
           "AND t.nextExecution <= :now ORDER BY t.priority DESC, t.nextExecution ASC")
    List<Task> findDueTasks(LocalDateTime now);
    
    List<Task> findByCreatedBy(String username);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    Long countByStatus(Task.TaskStatus status);
}
