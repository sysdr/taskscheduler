package com.scheduler.repository;

import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);
    List<Task> findTop10ByOrderByCreatedAtDesc();
}
