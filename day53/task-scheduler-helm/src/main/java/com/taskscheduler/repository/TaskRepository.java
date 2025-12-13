package com.taskscheduler.repository;

import com.taskscheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(String status);
    
    @Query("SELECT t FROM Task t ORDER BY t.priority DESC, t.createdAt ASC")
    List<Task> findAllOrderedByPriority();
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = ?1")
    Long countByStatus(String status);
}
