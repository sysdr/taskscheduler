package com.taskscheduler.batch.service;

import com.taskscheduler.batch.model.BatchMetrics;
import com.taskscheduler.batch.repository.BatchMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsService {
    
    private final BatchMetricsRepository metricsRepository;
    
    public List<BatchMetrics> getRecentBatches() {
        return metricsRepository.findTop100ByOrderByStartTimeDesc();
    }
    
    public AggregateMetrics getAggregateMetrics() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        
        Double avgProcessingTime = metricsRepository.getAverageProcessingTime(oneHourAgo);
        Long totalTasks = metricsRepository.getTotalTasksProcessed(oneHourAgo);
        
        return new AggregateMetrics(
                avgProcessingTime != null ? avgProcessingTime : 0.0,
                totalTasks != null ? totalTasks : 0L
        );
    }
    
    public record AggregateMetrics(double avgProcessingTimeMs, long totalTasksLastHour) {}
}
