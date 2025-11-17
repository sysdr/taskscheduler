package com.scheduler.repository;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByTaskId(String taskId);
    List<Task> findByTenantId(String tenantId);
    List<Task> findByTenantIdAndStatus(String tenantId, TaskStatus status);
    List<Task> findByStatusAndScheduledForBefore(TaskStatus status, LocalDateTime time);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.tenantId = ?1 AND t.status = ?2")
    long countByTenantIdAndStatus(String tenantId, TaskStatus status);
}
