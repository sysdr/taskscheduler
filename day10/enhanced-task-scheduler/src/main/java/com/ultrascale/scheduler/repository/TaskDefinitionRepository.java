package com.ultrascale.scheduler.repository;

import com.ultrascale.scheduler.model.TaskDefinition;
import com.ultrascale.scheduler.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {
    
    List<TaskDefinition> findByType(TaskType type);
    
    List<TaskDefinition> findByActiveTrue();
    
    long countByActiveTrue();
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.active = true AND t.cronExpression IS NOT NULL")
    List<TaskDefinition> findActiveScheduledTasks();
    
    @Query("SELECT t FROM TaskDefinition t WHERE t.name LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<TaskDefinition> searchByKeyword(@Param("keyword") String keyword);
}
