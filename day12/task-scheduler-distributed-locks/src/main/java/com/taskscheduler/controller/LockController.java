package com.taskscheduler.controller;

import com.taskscheduler.dto.LockStatusResponse;
import com.taskscheduler.service.DistributedLockService;
import com.taskscheduler.service.LockInfo;
import com.taskscheduler.service.LockStatistics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locks")
@CrossOrigin(origins = "*")
public class LockController {
    
    private final DistributedLockService lockService;
    
    public LockController(DistributedLockService lockService) {
        this.lockService = lockService;
    }
    
    @GetMapping("/status")
    public ResponseEntity<LockStatusResponse> getLockStatus() {
        LockStatistics stats = lockService.getStatistics();
        // For demo purposes, we'll return empty active locks list
        // In production, you'd query all active locks from repository
        List<LockStatusResponse.LockDetails> activeLocks = List.of();
        
        return ResponseEntity.ok(new LockStatusResponse(stats, activeLocks));
    }
    
    @GetMapping("/info/{lockKey}")
    public ResponseEntity<LockInfo> getLockInfo(@PathVariable String lockKey) {
        LockInfo info = lockService.getLockInfo(lockKey);
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/is-held/{lockKey}")
    public ResponseEntity<Map<String, Object>> isLockHeld(@PathVariable String lockKey) {
        boolean held = lockService.isLockHeld(lockKey);
        return ResponseEntity.ok(Map.of(
            "lockKey", lockKey,
            "isHeld", held
        ));
    }
    
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupExpiredLocks() {
        int cleaned = lockService.cleanupExpiredLocks();
        return ResponseEntity.ok(Map.of(
            "cleanedLocks", cleaned,
            "timestamp", java.time.LocalDateTime.now()
        ));
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<LockStatistics> getStatistics() {
        return ResponseEntity.ok(lockService.getStatistics());
    }
}
