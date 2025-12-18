package com.scheduler.repository;

import com.scheduler.model.ScheduledTask;
import com.scheduler.model.ScheduledTask.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
    List<ScheduledTask> findByTenantId(String tenantId);
    List<ScheduledTask> findByTenantIdAndStatus(String tenantId, TaskStatus status);
    
    @Query("SELECT t FROM ScheduledTask t WHERE t.tenantId = ?1 AND t.status = ?2 AND t.nextRunTime <= ?3")
    List<ScheduledTask> findTasksToExecute(String tenantId, TaskStatus status, LocalDateTime now);
    
    long countByTenantIdAndStatus(String tenantId, TaskStatus status);
}
