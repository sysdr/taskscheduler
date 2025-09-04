package com.taskscheduler.repository;

import com.taskscheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    @Query("SELECT t FROM Task t WHERE t.status = 'PENDING' ORDER BY t.createdAt ASC")
    List<Task> findPendingTasks();
    
    @Query("SELECT t FROM Task t WHERE t.status = 'RUNNING' ORDER BY t.startedAt DESC")
    List<Task> findRunningTasks();
    
    @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
    List<Task> findAllOrderByCreatedAtDesc();
    
    long countByStatus(String status);
}
