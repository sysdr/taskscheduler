package com.scheduler.controller;

import com.scheduler.model.DeadLetterTask;
import com.scheduler.model.FailureReason;
import com.scheduler.service.DeadLetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dlq")
@CrossOrigin(origins = "*")
public class DeadLetterController {
    
    @Autowired
    private DeadLetterService deadLetterService;
    
    @GetMapping
    public ResponseEntity<Page<DeadLetterTask>> getUnprocessedTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<DeadLetterTask> tasks = deadLetterService.getUnprocessedTasks(pageable);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DeadLetterTask> getDeadLetterTask(@PathVariable String id) {
        Optional<DeadLetterTask> task = deadLetterService.getById(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/failure-reason/{reason}")
    public ResponseEntity<List<DeadLetterTask>> getTasksByFailureReason(@PathVariable FailureReason reason) {
        List<DeadLetterTask> tasks = deadLetterService.getTasksByFailureReason(reason);
        return ResponseEntity.ok(tasks);
    }
    
    @PostMapping("/{id}/reprocess")
    public ResponseEntity<String> reprocessTask(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> request) {
        
        try {
            String notes = request != null ? request.get("notes") : "Manually reprocessed";
            deadLetterService.reprocessTask(id, notes);
            return ResponseEntity.ok("Task reprocessed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error reprocessing task: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Long> failureStats = deadLetterService.getFailureReasonStats();
        long unprocessedCount = deadLetterService.getUnprocessedTaskCount();
        
        return ResponseEntity.ok(Map.of(
            "unprocessedCount", unprocessedCount,
            "failureReasonStats", failureStats
        ));
    }
}
