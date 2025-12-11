package com.scheduler.config;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String,Object> t = new RedisTemplate<>();
        t.setConnectionFactory(factory);
        t.setKeySerializer(new StringRedisSerializer());
        t.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return t;
    }
}
