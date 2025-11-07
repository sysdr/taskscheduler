package com.taskscheduler.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class TaskConsumerService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${consumer.instance.name:unknown}")
    private String instanceName;
    
    public TaskConsumerService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    private String getConsumerId() {
        if (instanceName != null && !instanceName.isEmpty() && !instanceName.equals("unknown")) {
            return instanceName;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "consumer-" + System.currentTimeMillis();
        }
    }
    
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumeTask(Task task) {
        LocalDateTime startTime = LocalDateTime.now();
        String currentConsumerId = getConsumerId();
        
        System.out.println("🔷 [" + currentConsumerId + "] Processing Task: " + task.getId() + 
                          " [" + task.getType() + "]");
        
        // Update task status
        redisTemplate.opsForValue().set("task:" + task.getId() + ":status", "PROCESSING");
        redisTemplate.opsForValue().set("task:" + task.getId() + ":consumer", currentConsumerId);
        redisTemplate.opsForValue().increment("stats:" + currentConsumerId + ":processed");
        
        try {
            // Simulate task processing
            Thread.sleep(task.getComplexityMs());
            
            // Task completed successfully
            redisTemplate.opsForValue().set("task:" + task.getId() + ":status", "COMPLETED");
            
            LocalDateTime endTime = LocalDateTime.now();
            long duration = Duration.between(startTime, endTime).toMillis();
            
            System.out.println("✅ [" + getConsumerId() + "] Completed Task: " + task.getId() + 
                              " in " + duration + "ms");
            
            redisTemplate.opsForValue().increment("stats:total:completed");
            
        } catch (InterruptedException e) {
            currentConsumerId = getConsumerId();
            System.err.println("❌ [" + currentConsumerId + "] Failed Task: " + task.getId());
            redisTemplate.opsForValue().set("task:" + task.getId() + ":status", "FAILED");
            redisTemplate.opsForValue().increment("stats:" + currentConsumerId + ":failed");
            Thread.currentThread().interrupt();
        }
    }
}
