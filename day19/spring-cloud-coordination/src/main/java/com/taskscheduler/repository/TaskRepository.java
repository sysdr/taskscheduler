package com.taskscheduler.repository;

import com.taskscheduler.model.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<ScheduledTask, Long> {
    
    @Query("SELECT t FROM ScheduledTask t WHERE t.status = 'PENDING' AND t.scheduledTime <= :now ORDER BY t.scheduledTime ASC")
    List<ScheduledTask> findPendingTasksBeforeTime(LocalDateTime now);
    
    List<ScheduledTask> findByStatus(String status);
    
    @Query("SELECT t FROM ScheduledTask t WHERE t.executorInstance = :instance AND t.status = 'RUNNING'")
    List<ScheduledTask> findRunningTasksByInstance(String instance);
    
    long countByStatus(String status);
}
