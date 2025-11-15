package com.taskscheduler.repository;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByEventType(String eventType);
    List<Task> findTop20ByOrderByCreatedAtDesc();
}
