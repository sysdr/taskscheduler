package com.taskscheduler.alerting.service;

import com.taskscheduler.alerting.model.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class AlertEvaluationService {
    
    private final AlertNotificationService notificationService;
    private final MeterRegistry meterRegistry;
    private final Map<String, Alert> activeAlerts = new ConcurrentHashMap<>();
    private final Map<String, Long> alertHistory = new ConcurrentHashMap<>();
    
    @Value("${alerting.rules.task-failure-rate-threshold}")
    private double failureRateThreshold;
    
    @Value("${alerting.rules.task-failure-duration-minutes}")
    private int failureDurationMinutes;
    
    @Value("${alerting.rules.task-duration-p95-threshold-ms}")
    private long durationThresholdMs;
    
    @Value("${alerting.rules.queue-depth-threshold}")
    private long queueDepthThreshold;

    @Value("${alerting.demo-data.enabled:true}")
    private boolean demoDataEnabled;
    
    private final AtomicLong simulatedFailures = new AtomicLong(0);
    private final AtomicLong simulatedTasks = new AtomicLong(0);
    private final AtomicLong simulatedQueueDepth = new AtomicLong(0);
    
    public AlertEvaluationService(AlertNotificationService notificationService, MeterRegistry meterRegistry) {
        this.notificationService = notificationService;
        this.meterRegistry = meterRegistry;
        
        // Register custom metrics
        Gauge.builder("task.failures.current", simulatedFailures, AtomicLong::get)
            .description("Current number of task failures")
            .register(meterRegistry);
        
        Gauge.builder("task.queue.depth", simulatedQueueDepth, AtomicLong::get)
            .description("Current task queue depth")
            .register(meterRegistry);
        
        Counter.builder("alerts.triggered.total")
            .description("Total alerts triggered")
            .register(meterRegistry);
    }

    @PostConstruct
    public void seedDemoAlerts() {
        if (!demoDataEnabled || !activeAlerts.isEmpty()) {
            return;
        }

        log.info("Seeding dashboard with demo alerts");
        List<Alert> demoAlerts = List.of(
            Alert.builder()
                .id("demo-queue-depth")
                .name("Critical Queue Depth")
                .severity(AlertSeverity.CRITICAL)
                .status(AlertStatus.FIRING)
                .description("Incoming tasks are backing up faster than workers can drain them.")
                .metric("task_queue_depth")
                .currentValue(queueDepthThreshold + 4200.0)
                .threshold((double) queueDepthThreshold)
                .triggeredAt(LocalDateTime.now().minusMinutes(3))
                .labels(Map.of("team", "ops", "service", "task-scheduler", "environment", "demo"))
                .runbookUrl("https://wiki.company.com/runbooks/queue-depth")
                .dashboardUrl("http://localhost:3000/d/task-scheduler")
                .build(),
            Alert.builder()
                .id("demo-failure-rate")
                .name("Elevated Failure Rate")
                .severity(AlertSeverity.HIGH)
                .status(AlertStatus.FIRING)
                .description("Retry storm detected on task-scheduler workers.")
                .metric("task_failure_rate")
                .currentValue(failureRateThreshold + 2.5)
                .threshold(failureRateThreshold)
                .triggeredAt(LocalDateTime.now().minusMinutes(8))
                .labels(Map.of("team", "ops", "service", "task-scheduler", "environment", "demo"))
                .runbookUrl("https://wiki.company.com/runbooks/task-failures")
                .dashboardUrl("http://localhost:3000/d/task-scheduler")
                .build(),
            Alert.builder()
                .id("demo-duration")
                .name("Slow Task Duration (P95)")
                .severity(AlertSeverity.MEDIUM)
                .status(AlertStatus.FIRING)
                .description("P95 latency drifting above the golden signal SLO.")
                .metric("task_duration_p95")
                .currentValue(durationThresholdMs + 1800.0)
                .threshold((double) durationThresholdMs)
                .triggeredAt(LocalDateTime.now().minusMinutes(15))
                .labels(Map.of("team", "performance", "service", "task-scheduler", "environment", "demo"))
                .runbookUrl("https://wiki.company.com/runbooks/performance")
                .dashboardUrl("http://localhost:3000/d/task-scheduler")
                .build()
        );

        demoAlerts.forEach(alert -> activeAlerts.put(alert.getId(), alert));
    }
    
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void evaluateAlerts() {
        log.debug("Evaluating alert rules...");
        
        TaskMetrics metrics = collectMetrics();
        
        // Evaluate failure rate alert
        evaluateFailureRateAlert(metrics);
        
        // Evaluate task duration alert
        evaluateDurationAlert(metrics);
        
        // Evaluate queue depth alert
        evaluateQueueDepthAlert(metrics);
        
        // Check for resolved alerts
        checkResolvedAlerts(metrics);
    }
    
    private TaskMetrics collectMetrics() {
        // Simulate realistic task metrics
        long total = simulatedTasks.addAndGet((long)(Math.random() * 100 + 50));
        long failures = simulatedFailures.get();
        long queueDepth = simulatedQueueDepth.get();
        
        // Randomly inject failures to trigger alerts
        if (Math.random() < 0.1) {  // 10% chance to inject high failure rate
            simulatedFailures.set((long)(total * 0.15));  // 15% failure rate
        } else if (Math.random() < 0.3) {  // 30% chance to reduce failures
            simulatedFailures.set(Math.max(0, failures - (long)(Math.random() * 10)));
        }
        
        // Simulate queue depth fluctuations
        if (Math.random() < 0.05) {  // 5% chance of queue buildup
            simulatedQueueDepth.set(queueDepthThreshold + (long)(Math.random() * 5000));
        } else if (Math.random() < 0.3) {
            simulatedQueueDepth.set(Math.max(0, queueDepth - (long)(Math.random() * 1000)));
        }
        
        double failureRate = total > 0 ? (failures * 100.0 / total) : 0;
        
        return TaskMetrics.builder()
            .totalTasks(total)
            .failedTasks(failures)
            .completedTasks(total - failures)
            .failureRate(failureRate)
            .p95DurationMs((long)(2000 + Math.random() * 8000))
            .queueDepth(simulatedQueueDepth.get())
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    private void evaluateFailureRateAlert(TaskMetrics metrics) {
        String alertId = "task-failure-rate";
        
        if (metrics.getFailureRate() > failureRateThreshold) {
            long firstSeenTime = alertHistory.computeIfAbsent(alertId, k -> System.currentTimeMillis());
            long minutesSinceFirst = (System.currentTimeMillis() - firstSeenTime) / (60 * 1000);
            
            // Only fire if condition persists for configured duration
            if (minutesSinceFirst >= failureDurationMinutes && !activeAlerts.containsKey(alertId)) {
                Alert alert = Alert.builder()
                    .id(alertId)
                    .name("High Task Failure Rate")
                    .severity(AlertSeverity.HIGH)
                    .status(AlertStatus.FIRING)
                    .description(String.format("Task failure rate is %.2f%%, exceeding threshold of %.2f%%", 
                        metrics.getFailureRate(), failureRateThreshold))
                    .metric("task_failure_rate")
                    .currentValue(metrics.getFailureRate())
                    .threshold(failureRateThreshold)
                    .triggeredAt(LocalDateTime.now())
                    .labels(Map.of("team", "ops", "service", "task-scheduler"))
                    .runbookUrl("https://wiki.company.com/runbooks/task-failures")
                    .dashboardUrl("http://localhost:3000/d/task-scheduler")
                    .build();
                
                activeAlerts.put(alertId, alert);
                notificationService.sendAlert(alert);
                meterRegistry.counter("alerts.triggered.total", "alert", alertId).increment();
                
                log.warn("ALERT FIRED: {} - {}", alert.getName(), alert.getDescription());
            }
        } else {
            alertHistory.remove(alertId);
        }
    }
    
    private void evaluateDurationAlert(TaskMetrics metrics) {
        String alertId = "task-duration-high";
        
        if (metrics.getP95DurationMs() > durationThresholdMs && !activeAlerts.containsKey(alertId)) {
            Alert alert = Alert.builder()
                .id(alertId)
                .name("High Task Duration (P95)")
                .severity(AlertSeverity.MEDIUM)
                .status(AlertStatus.FIRING)
                .description(String.format("P95 task duration is %dms, exceeding threshold of %dms", 
                    metrics.getP95DurationMs(), durationThresholdMs))
                .metric("task_duration_p95")
                .currentValue((double)metrics.getP95DurationMs())
                .threshold((double)durationThresholdMs)
                .triggeredAt(LocalDateTime.now())
                .labels(Map.of("team", "performance", "service", "task-scheduler"))
                .runbookUrl("https://wiki.company.com/runbooks/performance")
                .dashboardUrl("http://localhost:3000/d/task-scheduler")
                .build();
            
            activeAlerts.put(alertId, alert);
            notificationService.sendAlert(alert);
            meterRegistry.counter("alerts.triggered.total", "alert", alertId).increment();
            
            log.warn("ALERT FIRED: {} - {}", alert.getName(), alert.getDescription());
        }
    }
    
    private void evaluateQueueDepthAlert(TaskMetrics metrics) {
        String alertId = "queue-depth-critical";
        
        if (metrics.getQueueDepth() > queueDepthThreshold && !activeAlerts.containsKey(alertId)) {
            Alert alert = Alert.builder()
                .id(alertId)
                .name("Critical Queue Depth")
                .severity(AlertSeverity.CRITICAL)
                .status(AlertStatus.FIRING)
                .description(String.format("Task queue depth is %d, exceeding threshold of %d. Consumers may be down.", 
                    metrics.getQueueDepth(), queueDepthThreshold))
                .metric("task_queue_depth")
                .currentValue((double)metrics.getQueueDepth())
                .threshold((double)queueDepthThreshold)
                .triggeredAt(LocalDateTime.now())
                .labels(Map.of("team", "ops", "service", "task-scheduler", "severity", "critical"))
                .runbookUrl("https://wiki.company.com/runbooks/queue-depth")
                .dashboardUrl("http://localhost:3000/d/task-scheduler")
                .build();
            
            activeAlerts.put(alertId, alert);
            notificationService.sendAlert(alert);
            meterRegistry.counter("alerts.triggered.total", "alert", alertId).increment();
            
            log.error("CRITICAL ALERT: {} - {}", alert.getName(), alert.getDescription());
        }
    }
    
    private void checkResolvedAlerts(TaskMetrics metrics) {
        List<String> toResolve = new ArrayList<>();
        
        for (Map.Entry<String, Alert> entry : activeAlerts.entrySet()) {
            Alert alert = entry.getValue();
            boolean resolved = false;
            
            switch (alert.getId()) {
                case "task-failure-rate":
                    resolved = metrics.getFailureRate() <= failureRateThreshold;
                    break;
                case "task-duration-high":
                    resolved = metrics.getP95DurationMs() <= durationThresholdMs;
                    break;
                case "queue-depth-critical":
                    resolved = metrics.getQueueDepth() <= queueDepthThreshold;
                    break;
            }
            
            if (resolved) {
                alert.setStatus(AlertStatus.RESOLVED);
                alert.setResolvedAt(LocalDateTime.now());
                notificationService.sendAlert(alert);
                toResolve.add(entry.getKey());
                log.info("ALERT RESOLVED: {}", alert.getName());
            }
        }
        
        toResolve.forEach(activeAlerts::remove);
    }
    
    public List<Alert> getActiveAlerts() {
        return new ArrayList<>(activeAlerts.values());
    }
    
    public void acknowledgeAlert(String alertId) {
        Alert alert = activeAlerts.get(alertId);
        if (alert != null) {
            alert.setStatus(AlertStatus.ACKNOWLEDGED);
            log.info("Alert acknowledged: {}", alertId);
        }
    }
    
    public void silenceAlert(String alertId) {
        Alert alert = activeAlerts.get(alertId);
        if (alert != null) {
            alert.setStatus(AlertStatus.SILENCED);
            log.info("Alert silenced: {}", alertId);
        }
    }
}
