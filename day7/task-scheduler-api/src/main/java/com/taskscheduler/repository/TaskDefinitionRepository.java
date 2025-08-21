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
    
    // Find by name (case-insensitive)
    Optional<TaskDefinition> findByNameIgnoreCase(String name);
    
    // Find by status
    List<TaskDefinition> findByStatus(TaskDefinition.TaskStatus status);
    
    // Find by status with pagination
    Page<TaskDefinition> findByStatus(TaskDefinition.TaskStatus status, Pageable pageable);
    
    // Find by name containing (case-insensitive) with pagination
    Page<TaskDefinition> findByNameContainingIgnoreCase(String namePattern, Pageable pageable);
    
    // Find by creation date range
    @Query("SELECT t FROM TaskDefinition t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<TaskDefinition> findByCreatedAtBetween(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    // Complex search with multiple criteria
    @Query("SELECT t FROM TaskDefinition t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:namePattern IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))) AND " +
           "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR t.createdAt <= :endDate)")
    Page<TaskDefinition> findWithFilters(
        @Param("status") TaskDefinition.TaskStatus status,
        @Param("namePattern") String namePattern,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    // Count by status
    long countByStatus(TaskDefinition.TaskStatus status);
    
    // Check if name exists (excluding current id for updates)
    @Query("SELECT COUNT(t) > 0 FROM TaskDefinition t WHERE LOWER(t.name) = LOWER(:name) AND (:excludeId IS NULL OR t.id != :excludeId)")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("excludeId") Long excludeId);
}
