package com.taskscheduler.repository;

import com.taskscheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.status IN ('RUNNING', 'PENDING')")
    List<Task> findActiveOrPendingTasks();
    
    @Query("SELECT t FROM Task t WHERE t.status = 'SUSPENDED' OR t.status = 'SCHEDULED_FOR_RETRY'")
    List<Task> findTasksForRecovery();
    
    long countByStatus(Task.TaskStatus status);
}
