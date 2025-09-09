package com.scheduler.monitoring.service;

import com.scheduler.leader.service.LeaderElectionService;
import com.scheduler.task.service.TaskExecutionService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SchedulerMetricsService {
    
    private final LeaderElectionService leaderElectionService;
    private final TaskExecutionService taskExecutionService;
    private final MeterRegistry meterRegistry;
    
    public SchedulerMetricsService(LeaderElectionService leaderElectionService,
                                 TaskExecutionService taskExecutionService,
                                 MeterRegistry meterRegistry) {
        this.leaderElectionService = leaderElectionService;
        this.taskExecutionService = taskExecutionService;
        this.meterRegistry = meterRegistry;
        
        // Register gauges
        meterRegistry.gauge("scheduler.leadership.is_leader", 
                          Tags.of("instance", leaderElectionService.getInstanceId()),
                          leaderElectionService, 
                          service -> service.isLeader() ? 1.0 : 0.0);
                          
        meterRegistry.gauge("scheduler.tasks.completed", 
                          Tags.of("instance", leaderElectionService.getInstanceId()),
                          taskExecutionService,
                          TaskExecutionService::getTaskCounter);
    }
    
    public Map<String, Object> getSchedulerStatus() {
        return Map.of(
            "instanceId", leaderElectionService.getInstanceId(),
            "isLeader", leaderElectionService.isLeader(),
            "state", leaderElectionService.getCurrentState(),
            "tasksCompleted", taskExecutionService.getTaskCounter()
        );
    }
}
