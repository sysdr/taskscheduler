package com.taskscheduler.repository;

import com.taskscheduler.model.TaskDefinition;
import com.taskscheduler.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TaskDefinition entity.
 * Provides data access methods optimized for distributed scheduling scenarios.
 */
@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, String> {
    
    /**
     * Find all tasks by status
     */
    List<TaskDefinition> findByStatus(TaskStatus status);
    
    /**
     * Find all tasks by task type
     */
    List<TaskDefinition> findByTaskType(String taskType);
    
    /**
     * Find all tasks by created by user
     */
    List<TaskDefinition> findByCreatedBy(String createdBy);
    
    /**
     * Find tasks eligible for execution (active and due to run)
     */
    @Query("SELECT t FROM TaskDefinition t WHERE t.status = :status AND t.nextRunTime <= :currentTime ORDER BY t.priority DESC, t.nextRunTime ASC")
    List<TaskDefinition> findEligibleTasks(@Param("status") TaskStatus status, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find tasks by priority range
     */
    @Query("SELECT t FROM TaskDefinition t WHERE t.priority BETWEEN :minPriority AND :maxPriority ORDER BY t.priority DESC")
    List<TaskDefinition> findByPriorityBetween(@Param("minPriority") Integer minPriority, @Param("maxPriority") Integer maxPriority);
    
    /**
     * Find active tasks ordered by next run time
     */
    @Query("SELECT t FROM TaskDefinition t WHERE t.status = 'ACTIVE' ORDER BY t.nextRunTime ASC")
    List<TaskDefinition> findActiveTasksOrderedByNextRun();
    
    /**
     * Find tasks that need attention (failed status)
     */
    @Query("SELECT t FROM TaskDefinition t WHERE t.status = 'FAILED' ORDER BY t.updatedAt DESC")
    List<TaskDefinition> findFailedTasks();
    
    /**
     * Count tasks by status
     */
    long countByStatus(TaskStatus status);
    
    /**
     * Find tasks with high priority (8-10)
     */
    @Query("SELECT t FROM TaskDefinition t WHERE t.priority >= 8 AND t.status = 'ACTIVE'")
    List<TaskDefinition> findHighPriorityTasks();
    
    /**
     * Find task by name (case-insensitive)
     */
    Optional<TaskDefinition> findByNameIgnoreCase(String name);
}
