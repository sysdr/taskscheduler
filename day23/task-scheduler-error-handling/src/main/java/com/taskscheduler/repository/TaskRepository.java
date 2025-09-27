package com.taskscheduler.repository;

import com.taskscheduler.entity.Task;
import com.taskscheduler.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(TaskStatus status);
    
    Page<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status, Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.createdAt >= :since")
    List<Task> findByStatusAndCreatedAtAfter(TaskStatus status, LocalDateTime since);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'RUNNING' AND t.startedAt < :threshold")
    List<Task> findStuckTasks(LocalDateTime threshold);
}
