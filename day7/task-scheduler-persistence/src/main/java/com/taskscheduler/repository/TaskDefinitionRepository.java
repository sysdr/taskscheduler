package com.taskscheduler.repository;

import com.taskscheduler.entity.TaskDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {
    
    Optional<TaskDefinition> findByName(String name);
    
    List<TaskDefinition> findByType(String type);
    
    List<TaskDefinition> findByStatus(TaskDefinition.TaskStatus status);
    
    List<TaskDefinition> findByEnabledTrue();
    
    List<TaskDefinition> findByStatusAndEnabledTrue(TaskDefinition.TaskStatus status);
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.name LIKE %:namePattern%")
    List<TaskDefinition> findByNameContaining(@Param("namePattern") String namePattern);
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<TaskDefinition> findByCreatedAtBetween(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    Page<TaskDefinition> findByTypeAndStatus(
        String type, 
        TaskDefinition.TaskStatus status, 
        Pageable pageable
    );
    
    @Query("SELECT COUNT(t) FROM TaskDefinition t WHERE t.status = :status")
    long countByStatus(@Param("status") TaskDefinition.TaskStatus status);
    
    boolean existsByName(String name);
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.retryCount > :threshold")
    List<TaskDefinition> findTasksWithHighRetryCount(@Param("threshold") int threshold);
}
