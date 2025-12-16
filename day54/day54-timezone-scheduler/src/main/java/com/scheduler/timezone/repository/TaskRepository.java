package com.scheduler.timezone.repository;

import com.scheduler.timezone.model.Task;
import com.scheduler.timezone.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByNextRunUtcBeforeAndStatus(Instant time, TaskStatus status);
    List<Task> findByStatusOrderByNextRunUtcAsc(TaskStatus status);
    List<Task> findByTimeZone(String timeZone);
}
