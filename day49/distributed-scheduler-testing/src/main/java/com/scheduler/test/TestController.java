package com.scheduler.test;

import com.scheduler.leader.LeaderElection;
import com.scheduler.lock.DistributedLock;
import com.scheduler.task.TaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {
    private final DistributedLock lock;
    private final LeaderElection election;
    private final TaskService taskService;
    private final String instanceId;

    public TestController(DistributedLock lock, LeaderElection election, TaskService taskService,
                         @Value("${scheduler.instance.id}") String instanceId) {
        this.lock = lock;
        this.election = election;
        this.taskService = taskService;
        this.instanceId = instanceId;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("instanceId", instanceId);
        status.put("isLeader", election.isLeader(instanceId));
        status.put("currentLeader", election.getCurrentLeader());
        status.put("tasksExecuted", taskService.getTasksExecutedBy(instanceId));
        status.put("pendingTasks", taskService.getPendingTasks().size());
        return ResponseEntity.ok(status);
    }

    @PostMapping("/lock/acquire")
    public ResponseEntity<Map<String, Object>> acquireLock(@RequestParam String key) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("acquired", lock.tryLock(key, instanceId, Duration.ofSeconds(30)));
        resp.put("instanceId", instanceId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/lock/release")
    public ResponseEntity<Map<String, Object>> releaseLock(@RequestParam String key) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("released", lock.unlock(key, instanceId));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/leader/elect")
    public ResponseEntity<Map<String, Object>> electLeader() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("elected", election.electLeader(instanceId));
        resp.put("currentLeader", election.getCurrentLeader());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/tasks")
    public ResponseEntity<Object> getTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PostMapping("/tasks")
    public ResponseEntity<Object> createTask(@RequestParam String name, @RequestParam String description) {
        return ResponseEntity.ok(taskService.createTask(name, description));
    }

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }
}
