package com.scheduler.metrics;
import com.scheduler.service.*;
import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;
@Service
public class CustomMetrics {
    public CustomMetrics(MeterRegistry reg, TaskQueueService queue, AutoScalingService scaler) {
        Gauge.builder("queue.depth", queue, TaskQueueService::getQueueDepth).register(reg);
        Gauge.builder("tasks.processing", queue, TaskQueueService::getProcessingCount).register(reg);
        Gauge.builder("instances.active", scaler, AutoScalingService::getCurrentInstances).register(reg);
    }
}
