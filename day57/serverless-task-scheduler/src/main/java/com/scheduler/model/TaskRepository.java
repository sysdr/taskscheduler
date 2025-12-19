package com.scheduler.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByExecutionMode(ExecutionMode mode);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'LAMBDA_INVOKED' AND t.startedAt < CURRENT_TIMESTAMP - 5 MINUTE")
    List<Task> findStaleLambdaTasks();
    
    @Query("SELECT t.type, COUNT(t), AVG(t.executionTimeMs), SUM(t.estimatedCost) FROM Task t WHERE t.executionMode = 'LAMBDA' GROUP BY t.type")
    List<Object[]> getLambdaExecutionStats();
}
