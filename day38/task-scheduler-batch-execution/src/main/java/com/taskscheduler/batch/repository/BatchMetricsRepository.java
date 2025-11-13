package com.taskscheduler.batch.repository;

import com.taskscheduler.batch.model.BatchMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BatchMetricsRepository extends JpaRepository<BatchMetrics, Long> {
    
    List<BatchMetrics> findTop100ByOrderByStartTimeDesc();
    
    @Query("SELECT AVG(bm.avgTaskProcessingTimeMs) FROM BatchMetrics bm WHERE bm.startTime > :since")
    Double getAverageProcessingTime(LocalDateTime since);
    
    @Query("SELECT SUM(bm.batchSize) FROM BatchMetrics bm WHERE bm.startTime > :since")
    Long getTotalTasksProcessed(LocalDateTime since);
}
