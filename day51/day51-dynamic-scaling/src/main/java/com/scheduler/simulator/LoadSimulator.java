package com.scheduler.simulator;
import com.scheduler.model.Task;
import com.scheduler.service.TaskQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;
@Component
public class LoadSimulator {
    private final TaskQueueService queue;
    private int rate = 5;
    public LoadSimulator(TaskQueueService queue) { this.queue = queue; }
    
    @Scheduled(fixedDelay = 1000)
    public void generate() {
        for (int i=0; i<rate; i++) {
            queue.submit(Task.builder()
                .id("TASK-"+UUID.randomUUID().toString().substring(0,8))
                .type("COMPUTE")
                .createdAt(LocalDateTime.now())
                .build());
        }
    }
    
    @Scheduled(fixedDelay = 30000)
    public void changePattern() {
        double r = Math.random();
        rate = r < 0.3 ? 5 : r < 0.6 ? 50 : 2;
        System.out.println("📊 Load: " + rate + " tasks/sec");
    }
}
