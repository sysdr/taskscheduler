package com.taskscheduler.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class MonitoringService {
    
    private final MeterRegistry meterRegistry;
    private final ThreadPoolTaskScheduler customTaskScheduler;
    private final ScheduledTasksService scheduledTasksService;
    private final Counter taskExecutionCounter;
    
    public MonitoringService(MeterRegistry meterRegistry,
                           @Qualifier("customTaskScheduler") ThreadPoolTaskScheduler customTaskScheduler,
                           ScheduledTasksService scheduledTasksService) {
        this.meterRegistry = meterRegistry;
        this.customTaskScheduler = customTaskScheduler;
        this.scheduledTasksService = scheduledTasksService;
        this.taskExecutionCounter = Counter.builder("scheduled.tasks.executed")
                .description("Total number of scheduled tasks executed")
                .register(meterRegistry);
    }
    
    @PostConstruct
    public void setupMetrics() {
        // Thread pool metrics
        Gauge.builder("thread.pool.active.threads", this, MonitoringService::getActiveThreads)
             .description("Number of active threads in the pool")
             .register(meterRegistry);
             
        Gauge.builder("thread.pool.pool.size", this, MonitoringService::getPoolSize)
             .description("Current pool size")
             .register(meterRegistry);
             
        Gauge.builder("thread.pool.queue.size", this, MonitoringService::getQueueSize)
             .description("Number of tasks in queue")
             .register(meterRegistry);
    }
    
    private double getActiveThreads() {
        ThreadPoolExecutor executor = customTaskScheduler.getScheduledThreadPoolExecutor();
        return executor != null ? executor.getActiveCount() : 0;
    }
    
    private double getPoolSize() {
        ThreadPoolExecutor executor = customTaskScheduler.getScheduledThreadPoolExecutor();
        return executor != null ? executor.getPoolSize() : 0;
    }
    
    private double getQueueSize() {
        ThreadPoolExecutor executor = customTaskScheduler.getScheduledThreadPoolExecutor();
        return executor != null ? executor.getQueue().size() : 0;
    }
    
    public ThreadPoolStats getThreadPoolStats() {
        ThreadPoolExecutor executor = customTaskScheduler.getScheduledThreadPoolExecutor();
        if (executor == null) {
            return new ThreadPoolStats(0, 0, 0, 0, 0);
        }
        
        return new ThreadPoolStats(
            executor.getActiveCount(),
            executor.getPoolSize(),
            executor.getCorePoolSize(),
            executor.getMaximumPoolSize(),
            executor.getQueue().size()
        );
    }
    
    public static class ThreadPoolStats {
        public final int activeThreads;
        public final int poolSize;
        public final int corePoolSize;
        public final int maxPoolSize;
        public final int queueSize;
        
        public ThreadPoolStats(int activeThreads, int poolSize, int corePoolSize, int maxPoolSize, int queueSize) {
            this.activeThreads = activeThreads;
            this.poolSize = poolSize;
            this.corePoolSize = corePoolSize;
            this.maxPoolSize = maxPoolSize;
            this.queueSize = queueSize;
        }
    }
}
