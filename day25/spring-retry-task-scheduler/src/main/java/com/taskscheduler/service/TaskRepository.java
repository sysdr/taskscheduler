package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByStatusAndScheduledAtBefore(TaskStatus status, LocalDateTime time);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'FAILED' AND t.retryCount < t.maxRetries")
    List<Task> findRetryableTasks();
    
    @Query("SELECT t FROM Task t WHERE t.status = 'FAILED' AND t.retryCount >= t.maxRetries")
    List<Task> findDeadLetterTasks();
}
