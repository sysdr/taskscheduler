package com.scheduler.controller;

import com.scheduler.model.Task;
import com.scheduler.service.*;
import com.scheduler.metrics.PerformanceMetrics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class SchedulerController {
    private final BaselineSchedulerService baselineService;
    private final BottleneckedSchedulerService bottleneckedService;
    private final OptimizedSchedulerService optimizedService;
    private final PerformanceMetrics metrics;

    public SchedulerController(BaselineSchedulerService baselineService,
                              BottleneckedSchedulerService bottleneckedService,
                              OptimizedSchedulerService optimizedService,
                              PerformanceMetrics metrics) {
        this.baselineService = baselineService;
        this.bottleneckedService = bottleneckedService;
        this.optimizedService = optimizedService;
        this.metrics = metrics;
    }

    @PostMapping("/tasks/{mode}")
    public CompletableFuture<ResponseEntity<Task>> submitTask(
            @PathVariable String mode,
            @RequestBody Map<String, String> request) {
        
        String name = request.get("name");
        String payload = request.get("payload");
        
        return switch (mode) {
            case "baseline" -> baselineService.submitTask(name, payload)
                .thenApply(ResponseEntity::ok);
            case "bottlenecked" -> bottleneckedService.submitTask(name, payload)
                .thenApply(ResponseEntity::ok);
            case "optimized" -> optimizedService.submitTask(name, payload)
                .thenApply(ResponseEntity::ok);
            default -> CompletableFuture.completedFuture(
                ResponseEntity.badRequest().build());
        };
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        metrics.updateHeapUsage();
        
        Runtime runtime = Runtime.getRuntime();
        return ResponseEntity.ok(Map.of(
            "tasksProcessed", metrics.getTasksProcessedCount(),
            "activeTasks", metrics.getActiveTasksCount(),
            "heapUsedMB", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024,
            "heapMaxMB", runtime.maxMemory() / 1024 / 1024,
            "availableProcessors", runtime.availableProcessors()
        ));
    }

    @PostMapping("/load-test/{mode}")
    public ResponseEntity<String> runLoadTest(@PathVariable String mode) {
        // Generate 100 concurrent tasks
        for (int i = 0; i < 100; i++) {
            submitTask(mode, Map.of(
                "name", "LoadTest-" + i,
                "payload", "Test payload " + i
            ));
        }
        return ResponseEntity.ok("Load test started with 100 tasks");
    }
}
