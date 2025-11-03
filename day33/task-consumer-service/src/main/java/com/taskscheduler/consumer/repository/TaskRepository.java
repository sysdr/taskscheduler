package com.taskscheduler.consumer.repository;

import com.taskscheduler.consumer.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByTaskId(String taskId);
    List<Task> findByStatus(Task.TaskStatus status);
    List<Task> findByWorkerId(String workerId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = ?1")
    long countByStatus(Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
    List<Task> findAllOrderByCreatedAtDesc();
}
