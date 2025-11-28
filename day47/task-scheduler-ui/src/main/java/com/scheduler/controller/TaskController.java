package com.scheduler.controller;

import com.scheduler.model.Task;
import com.scheduler.model.TaskExecution;
import com.scheduler.model.TaskStatus;
import com.scheduler.service.TaskMetrics;
import com.scheduler.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskService.getTaskById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task created = taskService.createTask(task);
        notifyClients();
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long id, 
            @RequestParam TaskStatus status) {
        Task updated = taskService.updateTaskStatus(id, status);
        notifyClients();
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        notifyClients();
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/executions")
    public ResponseEntity<List<TaskExecution>> getTaskExecutions(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskExecutions(id));
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<Void> executeTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        new Thread(() -> {
            taskService.executeTask(task);
            notifyClients();
        }).start();
        
        // Notify immediately that execution has started
        notifyClients();
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/next/execute")
    public ResponseEntity<Object> executeNextTask() {
        Optional<Task> nextTaskOpt = taskService.getNextScheduledTask();
        
        if (nextTaskOpt.isPresent()) {
            Task nextTask = nextTaskOpt.get();
            new Thread(() -> {
                taskService.executeTask(nextTask);
                notifyClients();
            }).start();
            
            // Notify immediately that execution has started
            notifyClients();
            
            return ResponseEntity.ok(nextTask);
        } else {
            return ResponseEntity.status(404)
                .body(java.util.Map.of("message", "No scheduled tasks available"));
        }
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<TaskMetrics> getMetrics() {
        return ResponseEntity.ok(taskService.getMetrics());
    }
    
    // Server-Sent Events endpoint for real-time updates
    @GetMapping("/stream")
    public SseEmitter streamUpdates() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        emitters.add(emitter);
        
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        
        return emitter;
    }
    
    private void notifyClients() {
        List<Task> tasks = taskService.getAllTasks();
        TaskMetrics metrics = taskService.getMetrics();
        
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("update")
                    .data(new UpdateData(tasks, metrics)));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
    
    record UpdateData(List<Task> tasks, TaskMetrics metrics) {}
}
