package com.scheduler.repository;

import com.scheduler.model.TaskDefinition;
import com.scheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {
    
    Optional<TaskDefinition> findByName(String name);
    
    List<TaskDefinition> findByStatus(TaskStatus status);
    
    @Query("SELECT td FROM TaskDefinition td WHERE td.status = 'ACTIVE' AND " +
           "(td.nextExecutionAt IS NULL OR td.nextExecutionAt <= :currentTime)")
    List<TaskDefinition> findEligibleTasks(LocalDateTime currentTime);
    
    @Query("SELECT COUNT(td) FROM TaskDefinition td WHERE td.status = 'ACTIVE'")
    long countActiveTasks();
    
    @Query("SELECT COUNT(td) FROM TaskDefinition td WHERE td.status = 'RUNNING'")
    long countRunningTasks();
}
