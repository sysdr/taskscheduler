package com.taskscheduler.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic fileEventsTopic() {
        return TopicBuilder.name("file-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name("user-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic systemEventsTopic() {
        return TopicBuilder.name("system-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deadLetterQueueTopic() {
        return TopicBuilder.name("dead-letter-queue")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
