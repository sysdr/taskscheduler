package com.scheduler.repository;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByStatusAndScheduledAtBefore(TaskStatus status, LocalDateTime dateTime);
    
    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.scheduledAt <= :now ORDER BY t.scheduledAt ASC")
    List<Task> findReadyTasks(@Param("status") TaskStatus status, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(@Param("status") TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'RETRYING' AND t.retryCount >= t.maxRetries")
    List<Task> findFailedTasks();
}
