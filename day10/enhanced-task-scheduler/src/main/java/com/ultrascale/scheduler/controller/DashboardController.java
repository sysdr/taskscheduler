package com.ultrascale.scheduler.controller;

import com.ultrascale.scheduler.model.TaskDefinition;
import com.ultrascale.scheduler.model.TaskResult;
import com.ultrascale.scheduler.model.TaskStatus;
import com.ultrascale.scheduler.repository.TaskDefinitionRepository;
import com.ultrascale.scheduler.repository.TaskResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private TaskDefinitionRepository taskDefinitionRepository;

    @Autowired
    private TaskResultRepository taskResultRepository;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Task counts
        long totalTasks = taskDefinitionRepository.count();
        long activeTasks = taskDefinitionRepository.countByActiveTrue();
        long inactiveTasks = totalTasks - activeTasks;
        
        // Results metrics
        List<TaskResult> allResults = taskResultRepository.findAll();
        
        // Status counts - these come from TaskResults, not TaskDefinitions
        long pendingTasks = 0;
        long runningTasks = 0;
        long completedTasks = 0;
        long failedTasks = 0;
        
        // Count tasks by status from results
        for (TaskResult result : allResults) {
            switch (result.getStatus()) {
                case PENDING:
                    pendingTasks++;
                    break;
                case RUNNING:
                    runningTasks++;
                    break;
                case COMPLETED:
                    completedTasks++;
                    break;
                case FAILED:
                    failedTasks++;
                    break;
            }
        }
        long successfulTasks = allResults.stream()
            .filter(result -> TaskStatus.COMPLETED.equals(result.getStatus()))
            .count();
        long failedResults = allResults.stream()
            .filter(result -> TaskStatus.FAILED.equals(result.getStatus()))
            .count();
        
        // Execution time metrics
        double avgExecutionTime = allResults.stream()
            .filter(result -> result.getExecutionTimeMs() != null)
            .mapToLong(TaskResult::getExecutionTimeMs)
            .average()
            .orElse(0.0);
        
        metrics.put("totalTasks", totalTasks);
        metrics.put("activeTasks", activeTasks);
        metrics.put("inactiveTasks", inactiveTasks);
        metrics.put("pendingTasks", pendingTasks);
        metrics.put("runningTasks", runningTasks);
        metrics.put("completedTasks", completedTasks);
        metrics.put("failedTasks", failedTasks);
        metrics.put("successfulExecutions", successfulTasks);
        metrics.put("failedExecutions", failedResults);
        metrics.put("averageExecutionTimeMs", avgExecutionTime);
        metrics.put("lastUpdated", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        long totalTasks = taskDefinitionRepository.count();
        long totalResults = taskResultRepository.count();
        
        summary.put("totalTasks", totalTasks);
        summary.put("totalExecutions", totalResults);
        summary.put("systemStatus", "RUNNING");
        summary.put("uptime", "Active");
        summary.put("lastCheck", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDefinition>> getAllTasks() {
        List<TaskDefinition> tasks = taskDefinitionRepository.findAll();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/results")
    public ResponseEntity<List<TaskResult>> getAllResults() {
        List<TaskResult> results = taskResultRepository.findAll();
        return ResponseEntity.ok(results);
    }
}
