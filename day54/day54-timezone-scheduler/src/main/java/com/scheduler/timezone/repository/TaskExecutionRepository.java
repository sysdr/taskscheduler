package com.scheduler.timezone.repository;

import com.scheduler.timezone.model.TaskExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, String> {
    List<TaskExecution> findByTaskIdOrderByExecutionTimeUtcDesc(String taskId);
    List<TaskExecution> findTop100ByOrderByExecutionTimeUtcDesc();
    List<TaskExecution> findByExecutionTimeUtcBetween(Instant start, Instant end);
}
