package com.taskscheduler.repository;

import com.taskscheduler.model.Task;
import com.taskscheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(User owner);
    List<Task> findByStatus(Task.TaskStatus status);
}
