package com.taskscheduler.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskSeeder {

    private final TaskProducerService taskProducerService;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${producer.startup.seed-count:50}")
    private int seedCount;

    public TaskSeeder(TaskProducerService taskProducerService,
                      RedisTemplate<String, String> redisTemplate) {
        this.taskProducerService = taskProducerService;
        this.redisTemplate = redisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedInitialTasks() {
        if (seedCount <= 0) {
            return;
        }

        String producedValue = redisTemplate.opsForValue().get("stats:total:produced");
        int producedCount = 0;
        if (producedValue != null) {
            try {
                producedCount = Integer.parseInt(producedValue);
            } catch (NumberFormatException ignored) {
                producedCount = 0;
            }
        }

        if (producedCount > 0) {
            System.out.println("ℹ️ Skipping task seeding; existing produced count = " + producedCount);
            return;
        }

        redisTemplate.opsForValue().setIfAbsent("stats:total:produced", "0");
        redisTemplate.opsForValue().setIfAbsent("stats:total:completed", "0");

        System.out.println("🌱 Seeding initial tasks: " + seedCount);
        taskProducerService.produceBatch(seedCount);
    }
}

