package com.scheduler.config;

import com.scheduler.model.TaskPriority;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String EXCHANGE_NAME = "task.exchange";
    
    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
    
    @Bean
    public Queue highPriorityQueue() {
        return new Queue(TaskPriority.HIGH.getQueueName(), true);
    }
    
    @Bean
    public Queue normalPriorityQueue() {
        return new Queue(TaskPriority.NORMAL.getQueueName(), true);
    }
    
    @Bean
    public Queue lowPriorityQueue() {
        return new Queue(TaskPriority.LOW.getQueueName(), true);
    }
    
    @Bean
    public Binding highPriorityBinding(Queue highPriorityQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(highPriorityQueue)
                .to(taskExchange)
                .with("task.priority.high");
    }
    
    @Bean
    public Binding normalPriorityBinding(Queue normalPriorityQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(normalPriorityQueue)
                .to(taskExchange)
                .with("task.priority.normal");
    }
    
    @Bean
    public Binding lowPriorityBinding(Queue lowPriorityQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(lowPriorityQueue)
                .to(taskExchange)
                .with("task.priority.low");
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
}
