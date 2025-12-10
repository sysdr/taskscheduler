package com.scheduler.chaos.service;

import com.scheduler.chaos.injection.ChaosInjector;
import com.scheduler.chaos.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class ChaosService {
    private final ChaosInjector chaosInjector;
    private final ChaosMetrics metrics = new ChaosMetrics();
    private final Map<String, ChaosExperiment> activeExperiments = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private String currentLeader = "node-1";
    private final List<String> availableNodes = Arrays.asList("node-1", "node-2", "node-3");
    
    public ChaosExperiment startExperiment(ChaosExperiment.ChaosType type, ChaosConfig config) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        
        ChaosExperiment experiment = new ChaosExperiment();
        experiment.setId(id);
        experiment.setType(type);
        experiment.setStatus(ChaosExperiment.ExperimentStatus.RUNNING);
        experiment.setStartTime(LocalDateTime.now());
        experiment.setConfig(config);
        experiment.setDescription(getDescription(type));
        
        activeExperiments.put(id, experiment);
        metrics.setChaosActive(true);
        
        // Inject chaos
        chaosInjector.injectChaos(type, config.getLatencyMs());
        
        // Auto-stop after duration
        scheduler.schedule(() -> stopExperiment(id), 
            config.getDurationSeconds(), TimeUnit.SECONDS);
        
        return experiment;
    }
    
    public void stopExperiment(String id) {
        ChaosExperiment experiment = activeExperiments.get(id);
        if (experiment != null) {
            experiment.setStatus(ChaosExperiment.ExperimentStatus.COMPLETED);
            experiment.setEndTime(LocalDateTime.now());
            chaosInjector.stopChaos();
            metrics.setChaosActive(false);
            
            if (chaosInjector.isLeaderKilled()) {
                electNewLeader();
            }
        }
    }
    
    public SystemHealth getSystemHealth() {
        return new SystemHealth(
            currentLeader,
            availableNodes.size(),
            metrics.getTasksProcessed().get(),
            metrics.getErrorRate(),
            metrics.getAvgLatency(),
            metrics.getErrorRate() < 5.0,
            metrics.isChaosActive() ? "CHAOS_ACTIVE" : "HEALTHY"
        );
    }
    
    public ChaosMetrics getMetrics() {
        return metrics;
    }
    
    public List<ChaosExperiment> getActiveExperiments() {
        return new ArrayList<>(activeExperiments.values());
    }
    
    public void simulateTask(String taskId) {
        long start = System.currentTimeMillis();
        
        try {
            // Simulate task processing with potential chaos
            chaosInjector.maybeInjectLatency();
            
            // Simulate failure based on chaos state
            boolean success = !chaosInjector.shouldDropPacket(
                chaosInjector.isNetworkPartitioned() ? 50 : 5
            );
            
            long latency = System.currentTimeMillis() - start;
            metrics.recordTask(success, latency);
            
        } catch (Exception e) {
            metrics.recordTask(false, System.currentTimeMillis() - start);
        }
    }
    
    private void electNewLeader() {
        // Simulate leader election
        metrics.getLeaderElections().incrementAndGet();
        currentLeader = availableNodes.get(new Random().nextInt(availableNodes.size()));
        System.out.println("✅ New leader elected: " + currentLeader);
    }
    
    private String getDescription(ChaosExperiment.ChaosType type) {
        return switch (type) {
            case LEADER_KILL -> "Terminate leader node and verify election";
            case NETWORK_PARTITION -> "Simulate network split between nodes";
            case LATENCY_INJECTION -> "Add artificial latency to operations";
            case DATABASE_SLOWDOWN -> "Inject delays in database queries";
            case RESOURCE_EXHAUSTION -> "Exhaust thread pool resources";
            case MESSAGE_QUEUE_SATURATION -> "Flood message queue with events";
            case SPLIT_BRAIN -> "Create multiple leader scenario";
            case CASCADE_FAILURE -> "Trigger cascading service failures";
        };
    }
}
