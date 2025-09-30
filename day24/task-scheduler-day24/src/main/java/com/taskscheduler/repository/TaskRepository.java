package com.taskscheduler.repository;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByStatusOrderByPriorityDescCreatedTimeAsc(TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.status = :status AND " +
           "(t.scheduledTime IS NULL OR t.scheduledTime <= :now)")
    List<Task> findReadyToExecute(@Param("status") TaskStatus status, 
                                  @Param("now") LocalDateTime now);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'FAILED' AND " +
           "t.attemptCount < t.maxRetries AND " +
           "(t.nextRetryTime IS NULL OR t.nextRetryTime <= :now)")
    List<Task> findRetriableTasks(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(@Param("status") TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.status IN :statuses")
    List<Task> findByStatusIn(@Param("statuses") List<TaskStatus> statuses);
    
    @Query("SELECT t FROM Task t WHERE t.createdTime >= :startTime AND t.createdTime <= :endTime")
    List<Task> findTasksInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
}
