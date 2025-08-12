package com.taskscheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class SchedulerConfig {
    
    @Value("${task.scheduler.pool-size:10}")
    private int poolSize;
    
    @Value("${task.scheduler.max-pool-size:20}")
    private int maxPoolSize;
    
    @Value("${task.scheduler.thread-name-prefix:custom-scheduler-}")
    private String threadNamePrefix;
    
    @Value("${task.scheduler.await-termination-seconds:20}")
    private int awaitTerminationSeconds;
    
    @Value("${task.scheduler.wait-for-tasks-to-complete-on-shutdown:true}")
    private boolean waitForTasksToCompleteOnShutdown;
    
    private ThreadPoolTaskScheduler customTaskScheduler;
    private ThreadPoolTaskScheduler defaultTaskScheduler;
    
    @Bean(name = "customTaskScheduler")
    @Primary
    public ThreadPoolTaskScheduler customTaskScheduler() {
        customTaskScheduler = new ThreadPoolTaskScheduler();
        customTaskScheduler.setPoolSize(poolSize);
        customTaskScheduler.setThreadNamePrefix(threadNamePrefix);
        customTaskScheduler.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        customTaskScheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
        customTaskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        return customTaskScheduler;
    }
    
    @Bean(name = "defaultTaskScheduler")
    public ThreadPoolTaskScheduler defaultTaskScheduler() {
        defaultTaskScheduler = new ThreadPoolTaskScheduler();
        defaultTaskScheduler.setPoolSize(1);
        defaultTaskScheduler.setThreadNamePrefix("default-scheduler-");
        
        return defaultTaskScheduler;
    }
    
    @EventListener(ContextRefreshedEvent.class)
    public void configureMaxPoolSizes() {
        // Configure max pool size for custom scheduler
        if (customTaskScheduler != null) {
            ThreadPoolExecutor executor = customTaskScheduler.getScheduledThreadPoolExecutor();
            if (executor != null) {
                executor.setMaximumPoolSize(maxPoolSize);
                System.out.println("✅ Custom scheduler max pool size set to: " + maxPoolSize);
            }
        }
        
        // Configure max pool size for default scheduler
        if (defaultTaskScheduler != null) {
            ThreadPoolExecutor executor = defaultTaskScheduler.getScheduledThreadPoolExecutor();
            if (executor != null) {
                executor.setMaximumPoolSize(5);
                System.out.println("✅ Default scheduler max pool size set to: 5");
            }
        }
    }
}
