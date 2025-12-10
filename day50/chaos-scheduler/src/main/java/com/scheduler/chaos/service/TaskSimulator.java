package com.scheduler.chaos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskSimulator {
    private final ChaosService chaosService;
    
    @Scheduled(fixedRate = 100) // Simulate 10 tasks/second
    public void generateTasks() {
        String taskId = UUID.randomUUID().toString().substring(0, 8);
        chaosService.simulateTask(taskId);
    }
}
