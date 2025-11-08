package com.scheduler.service;

import com.scheduler.config.RabbitMQConfig;
import com.scheduler.model.Task;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskPublisherService {
    
    private final RabbitTemplate rabbitTemplate;
    private final Counter highPriorityCounter;
    private final Counter normalPriorityCounter;
    private final Counter lowPriorityCounter;
    
    public TaskPublisherService(RabbitTemplate rabbitTemplate, MeterRegistry meterRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.highPriorityCounter = Counter.builder("tasks.submitted")
                .tag("priority", "high")
                .register(meterRegistry);
        this.normalPriorityCounter = Counter.builder("tasks.submitted")
                .tag("priority", "normal")
                .register(meterRegistry);
        this.lowPriorityCounter = Counter.builder("tasks.submitted")
                .tag("priority", "low")
                .register(meterRegistry);
    }
    
    public void publishTask(Task task) {
        String routingKey = "task.priority." + task.getPriority().name().toLowerCase();
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, task);
        
        switch (task.getPriority()) {
            case HIGH -> highPriorityCounter.increment();
            case NORMAL -> normalPriorityCounter.increment();
            case LOW -> lowPriorityCounter.increment();
        }
    }
}
