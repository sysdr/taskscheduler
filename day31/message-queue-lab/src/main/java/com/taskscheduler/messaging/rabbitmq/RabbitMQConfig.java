package com.taskscheduler.messaging.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange
    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange("task.exchange");
    }

    // Queues
    @Bean
    public Queue taskSubmissionQueue() {
        return QueueBuilder.durable("task.submission.queue").build();
    }

    @Bean
    public Queue taskExecutionQueue() {
        return QueueBuilder.durable("task.execution.queue").build();
    }

    @Bean
    public Queue taskResultQueue() {
        return QueueBuilder.durable("task.result.queue").build();
    }

    // Bindings
    @Bean
    public Binding taskSubmissionBinding() {
        return BindingBuilder.bind(taskSubmissionQueue())
                .to(taskExchange())
                .with("task.submission");
    }

    @Bean
    public Binding taskExecutionBinding() {
        return BindingBuilder.bind(taskExecutionQueue())
                .to(taskExchange())
                .with("task.execution");
    }

    @Bean
    public Binding taskResultBinding() {
        return BindingBuilder.bind(taskResultQueue())
                .to(taskExchange())
                .with("task.result");
    }

    // Message Converter
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
