package com.scheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {
    
    @Value("${scheduler.thread-pool.core-size:10}")
    private int corePoolSize;
    
    @Value("${scheduler.thread-pool.max-size:50}")
    private int maxPoolSize;
    
    @Value("${scheduler.thread-pool.queue-capacity:1000}")
    private int queueCapacity;

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("TimeoutTask-");
        executor.initialize();
        return executor;
    }
}
