package com.scheduler.metrics.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterBinder taskSchedulerMetrics() {
        return registry -> {
            // Custom meter binder for additional system metrics
            registry.gauge("jvm.thread.count", 
                Thread.activeCount());
        };
    }
}
