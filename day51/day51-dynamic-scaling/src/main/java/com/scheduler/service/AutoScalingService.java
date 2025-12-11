package com.scheduler.service;
import com.scheduler.model.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class AutoScalingService {
    private final TaskQueueService queue;
    private final RedisTemplate<String,Object> redis;
    private int instances = 1;
    private LocalDateTime lastScale = LocalDateTime.now();
    
    public AutoScalingService(TaskQueueService queue, RedisTemplate<String,Object> redis) {
        this.queue = queue;
        this.redis = redis;
    }
    
    public ScalingEvent evaluate() {
        int qd = queue.getQueueDepth();
        int proc = queue.getProcessingCount();
        double perInstance = (double)(qd + proc) / instances;
        
        ScalingAction action = ScalingAction.NO_ACTION;
        String reason = "Within thresholds";
        int before = instances;
        
        if (java.time.Duration.between(lastScale, LocalDateTime.now()).toMinutes() < 5) {
            reason = "Cooldown active";
        } else if (perInstance > 150 && instances < 10) {
            action = ScalingAction.SCALE_UP;
            instances = Math.min(instances + 2, 10);
            reason = String.format("High load: %.0f tasks/instance", perInstance);
            lastScale = LocalDateTime.now();
        } else if (perInstance < 30 && instances > 1) {
            action = ScalingAction.SCALE_DOWN;
            instances = Math.max(instances - 1, 1);
            reason = String.format("Low load: %.0f tasks/instance", perInstance);
            lastScale = LocalDateTime.now();
        }
        
        ScalingEvent event = ScalingEvent.builder()
            .id(UUID.randomUUID().toString())
            .action(action)
            .before(before)
            .after(instances)
            .reason(reason)
            .metric(perInstance)
            .timestamp(LocalDateTime.now())
            .build();
        
        if (action != ScalingAction.NO_ACTION) {
            redis.opsForList().rightPush("events", event);
            redis.opsForList().trim("events", -50, -1);
            System.out.println("⚡ " + action + ": " + before + " → " + instances + " (" + reason + ")");
        }
        
        return event;
    }
    
    public int getCurrentInstances() { return instances; }
    
    public List<ScalingEvent> getEvents() {
        List<Object> list = redis.opsForList().range("events", -20, -1);
        List<ScalingEvent> events = new ArrayList<>();
        if (list != null) for (Object o : list) events.add((ScalingEvent)o);
        return events;
    }
}
