package com.scheduler.service;
import com.scheduler.model.Task;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.*;
@Service
public class TaskProcessorService {
    private final TaskQueueService queue;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final String instanceId = UUID.randomUUID().toString().substring(0,8);
    
    public TaskProcessorService(TaskQueueService queue) { this.queue = queue; }
    
    @Scheduled(fixedDelay = 100)
    public void process() {
        for (int i=0; i<5; i++) {
            Task task = queue.poll();
            if (task != null) executor.submit(() -> handle(task));
        }
    }
    
    private void handle(Task task) {
        try {
            task.setInstanceId(instanceId);
            Thread.sleep(500 + (int)(Math.random() * 1500));
            queue.complete(task);
            System.out.println("✓ Completed: " + task.getId() + " [" + instanceId + "]");
        } catch (Exception e) {
            System.err.println("✗ Failed: " + task.getId());
        }
    }
    
    public String getInstanceId() { return instanceId; }
}
