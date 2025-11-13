package com.taskscheduler.batch.repository;

import com.taskscheduler.batch.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatusOrderByCreatedAtAsc(Task.TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(@Param("status") Task.TaskStatus status);
    
    @Modifying
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.status = :oldStatus AND t.createdAt < :timeout")
    int resetStuckTasks(@Param("oldStatus") Task.TaskStatus oldStatus, 
                       @Param("newStatus") Task.TaskStatus newStatus,
                       @Param("timeout") LocalDateTime timeout);
    
    List<Task> findByBatchIdOrderByCreatedAtAsc(String batchId);
}
