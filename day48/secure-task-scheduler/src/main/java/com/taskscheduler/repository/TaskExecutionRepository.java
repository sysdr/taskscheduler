package com.taskscheduler.repository;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.TaskExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    List<TaskExecution> findByTaskOrderByStartTimeDesc(Task task);
    List<TaskExecution> findTop10ByTaskOrderByStartTimeDesc(Task task);
}
