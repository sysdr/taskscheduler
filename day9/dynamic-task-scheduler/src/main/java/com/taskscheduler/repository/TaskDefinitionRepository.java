package com.taskscheduler.repository;

import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.entity.TaskDefinition.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {
    
    Optional<TaskDefinition> findByTaskName(String taskName);
    
    List<TaskDefinition> findByStatus(TaskStatus status);
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.status IN ('ACTIVE', 'PAUSED')")
    List<TaskDefinition> findActiveAndPausedTasks();
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.status = 'ACTIVE'")
    List<TaskDefinition> findActiveTasks();
    
    boolean existsByTaskName(String taskName);
}
