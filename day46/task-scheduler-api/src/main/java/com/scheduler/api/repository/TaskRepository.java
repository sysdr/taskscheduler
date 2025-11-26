package com.scheduler.api.repository;

import com.scheduler.api.model.Task;
import com.scheduler.api.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByName(String name);
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    Page<Task> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Task> findByStatusAndNameContainingIgnoreCase(TaskStatus status, String name, Pageable pageable);
}
