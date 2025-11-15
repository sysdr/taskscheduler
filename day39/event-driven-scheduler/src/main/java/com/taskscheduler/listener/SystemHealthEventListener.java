package com.taskscheduler.listener;

import com.taskscheduler.model.SystemHealthEvent;
import com.taskscheduler.service.EventTaskService;
import com.taskscheduler.service.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemHealthEventListener {
    
    private final EventTaskService eventTaskService;
    private final MetricsService metricsService;

    @KafkaListener(topics = "system-events", groupId = "task-scheduler-group")
    public void handleSystemHealthEvent(@Payload SystemHealthEvent event) {
        
        try {
            log.info("Received system health event: {} with value: {}", 
                    event.getMetricType(), event.getCurrentValue());
            
            metricsService.incrementSystemEvents();
            
            // Validate event
            if (!isValidSystemEvent(event)) {
                log.warn("Invalid system health event: {}", event.getEventId());
                eventTaskService.routeToDeadLetter(event, "Invalid event structure");
                return;
            }
            
            // Trigger tasks based on metric type and severity
            switch (event.getMetricType()) {
                case "CPU_HIGH":
                    if (event.getSeverity().equals("CRITICAL")) {
                        eventTaskService.triggerCpuCleanupTask(event);
                    }
                    break;
                case "DISK_LOW":
                    eventTaskService.triggerDiskCleanupTask(event);
                    eventTaskService.triggerArchivalTask(event);
                    break;
                case "MEMORY_HIGH":
                    eventTaskService.triggerMemoryCleanupTask(event);
                    break;
                default:
                    log.info("No task mapping for metric type: {}", event.getMetricType());
            }
            
        } catch (Exception e) {
            log.error("Error processing system health event: {}", event.getEventId(), e);
            eventTaskService.routeToDeadLetter(event, e.getMessage());
        }
    }
    
    private boolean isValidSystemEvent(SystemHealthEvent event) {
        return event.getMetricType() != null && 
               event.getCurrentValue() >= 0 && 
               event.getThreshold() > 0 &&
               event.getSeverity() != null;
    }
}
