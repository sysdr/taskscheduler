package com.scheduler.config;

import com.scheduler.manager.RedlockManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "redlock")
public class RedlockConfig {
    
    private List<RedisInstance> instances;
    private int retryCount = 3;
    private long retryDelay = 200;
    private double clockDriftFactor = 0.01;

    @Bean
    public RedlockManager redlockManager() {
        return new RedlockManager(instances, retryCount, retryDelay, clockDriftFactor);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    public static class RedisInstance {
        private String host;
        private int port;

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
    }

    // Getters and setters
    public List<RedisInstance> getInstances() { return instances; }
    public void setInstances(List<RedisInstance> instances) { this.instances = instances; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public long getRetryDelay() { return retryDelay; }
    public void setRetryDelay(long retryDelay) { this.retryDelay = retryDelay; }
    public double getClockDriftFactor() { return clockDriftFactor; }
    public void setClockDriftFactor(double clockDriftFactor) { this.clockDriftFactor = clockDriftFactor; }
}
