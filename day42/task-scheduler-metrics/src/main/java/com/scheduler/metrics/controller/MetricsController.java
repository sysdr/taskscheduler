package com.scheduler.metrics.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin(origins = "*")
public class MetricsController {
    
    private final MeterRegistry meterRegistry;
    
    public MetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Task counters
        summary.put("taskSubmitted", getCounterValue("task.submitted.total"));
        summary.put("taskCompleted", getCounterValue("task.completed.total"));
        summary.put("taskFailed", getCounterValue("task.failed.total"));
        
        // Task gauges
        summary.put("activeTasks", getGaugeValue("task.active.count"));
        summary.put("queuedTasks", getGaugeValue("task.queued.count"));
        summary.put("totalTasks", getGaugeValue("task.total.count"));
        
        // Calculate rates
        double completed = getCounterValue("task.completed.total");
        double failed = getCounterValue("task.failed.total");
        double total = completed + failed;
        
        summary.put("successRate", total > 0 ? (completed / total) * 100 : 0);
        summary.put("failureRate", total > 0 ? (failed / total) * 100 : 0);
        
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/timers")
    public ResponseEntity<List<Map<String, Object>>> getTimerMetrics() {
        List<Map<String, Object>> timers = new ArrayList<>();
        
        meterRegistry.find("task.execution").timers().forEach(timer -> {
            Map<String, Object> timerInfo = new HashMap<>();
            timerInfo.put("tags", timer.getId().getTags().stream()
                    .collect(Collectors.toMap(
                            tag -> tag.getKey(),
                            tag -> tag.getValue()
                    )));
            timerInfo.put("count", timer.count());
            timerInfo.put("totalTimeMs", timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS));
            timerInfo.put("meanMs", timer.mean(java.util.concurrent.TimeUnit.MILLISECONDS));
            timerInfo.put("maxMs", timer.max(java.util.concurrent.TimeUnit.MILLISECONDS));
            timers.add(timerInfo);
        });
        
        return ResponseEntity.ok(timers);
    }
    
    @GetMapping("/counters")
    public ResponseEntity<List<Map<String, Object>>> getCounterMetrics() {
        List<Map<String, Object>> counters = new ArrayList<>();
        
        meterRegistry.getMeters().stream()
                .filter(meter -> meter.getId().getName().startsWith("task."))
                .filter(meter -> meter instanceof io.micrometer.core.instrument.Counter)
                .forEach(meter -> {
                    Map<String, Object> counterInfo = new HashMap<>();
                    counterInfo.put("name", meter.getId().getName());
                    counterInfo.put("tags", meter.getId().getTags().stream()
                            .collect(Collectors.toMap(
                                    tag -> tag.getKey(),
                                    tag -> tag.getValue()
                            )));
                    counterInfo.put("count", ((io.micrometer.core.instrument.Counter) meter).count());
                    counters.add(counterInfo);
                });
        
        return ResponseEntity.ok(counters);
    }
    
    private double getCounterValue(String name) {
        var counter = meterRegistry.find(name).counter();
        return counter != null ? counter.count() : 0;
    }
    
    private double getGaugeValue(String name) {
        var gauge = meterRegistry.find(name).gauge();
        return gauge != null ? gauge.value() : 0;
    }
}
