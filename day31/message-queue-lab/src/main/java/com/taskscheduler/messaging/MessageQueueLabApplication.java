package com.taskscheduler.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableKafka
@EnableRabbit
public class MessageQueueLabApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageQueueLabApplication.class, args);
        System.out.println("🚀 Message Queue Lab Application Started!");
        System.out.println("📊 Dashboard: http://localhost:8090");
        System.out.println("🔧 Kafka UI: http://localhost:8080");
        System.out.println("🐰 RabbitMQ Management: http://localhost:15672");
    }
}
