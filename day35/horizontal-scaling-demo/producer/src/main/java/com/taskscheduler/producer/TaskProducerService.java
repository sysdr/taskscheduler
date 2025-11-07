package com.taskscheduler.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TaskProducerService {
    
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final Random random = new Random();
    
    @Value("${rabbitmq.queue.name}")
    private String queueName;
    
    public TaskProducerService(RabbitTemplate rabbitTemplate, 
                               RedisTemplate<String, String> redisTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
    }
    
    public void produceTask() {
        String taskId = UUID.randomUUID().toString().substring(0, 8);
        String[] types = {"EMAIL", "SMS", "PUSH_NOTIFICATION", "REPORT_GENERATION", "DATA_PROCESSING"};
        String type = types[random.nextInt(types.length)];
        int complexity = 500 + random.nextInt(1500); // 500ms to 2000ms
        
        Task task = new Task(taskId, type, "Processing " + type + " task", complexity);
        
        rabbitTemplate.convertAndSend(queueName, task);
        redisTemplate.opsForValue().set("task:" + taskId + ":status", "QUEUED");
        redisTemplate.opsForValue().increment("stats:total:produced");
        
        System.out.println("📤 Produced Task: " + taskId + " [" + type + "] complexity: " + complexity + "ms");
    }
    
    public void produceBatch(int count) {
        System.out.println("🚀 Producing " + count + " tasks...");
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < count; i++) {
            produceTask();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("✅ Produced " + count + " tasks in " + duration + "ms");
    }
}
