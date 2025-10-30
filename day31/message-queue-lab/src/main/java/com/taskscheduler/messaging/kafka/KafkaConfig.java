package com.taskscheduler.messaging.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic taskSubmissionTopic() {
        return new NewTopic("task-submission", 3, (short) 1);
    }

    @Bean
    public NewTopic taskExecutionTopic() {
        return new NewTopic("task-execution", 3, (short) 1);
    }

    @Bean
    public NewTopic taskResultTopic() {
        return new NewTopic("task-result", 3, (short) 1);
    }
}
