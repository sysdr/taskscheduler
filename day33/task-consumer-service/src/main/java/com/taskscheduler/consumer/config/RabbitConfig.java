package com.taskscheduler.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    
    public static final String TASK_EXECUTION_QUEUE = "task-execution-queue";
    public static final String TASK_STATUS_EXCHANGE = "task-status-exchange";
    public static final String TASK_STATUS_ROUTING_KEY = "task.status.update";
    
    @Bean
    public Queue taskExecutionQueue() {
        return QueueBuilder.durable(TASK_EXECUTION_QUEUE)
                .withArgument("x-message-ttl", 300000) // 5 minutes TTL
                .build();
    }
    
    @Bean
    public TopicExchange taskStatusExchange() {
        return new TopicExchange(TASK_STATUS_EXCHANGE);
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
