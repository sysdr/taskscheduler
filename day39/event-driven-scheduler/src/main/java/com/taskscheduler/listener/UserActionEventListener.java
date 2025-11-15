package com.taskscheduler.listener;

import com.taskscheduler.model.UserActionEvent;
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
public class UserActionEventListener {
    
    private final EventTaskService eventTaskService;
    private final MetricsService metricsService;

    @KafkaListener(topics = "user-events", groupId = "task-scheduler-group")
    public void handleUserActionEvent(@Payload UserActionEvent event) {
        
        try {
            log.info("Received user action event: {} for user: {}", 
                    event.getActionType(), event.getUsername());
            
            metricsService.incrementUserEvents();
            
            // Validate event
            if (!isValidUserEvent(event)) {
                log.warn("Invalid user action event: {}", event.getEventId());
                eventTaskService.routeToDeadLetter(event, "Invalid event structure");
                return;
            }
            
            // Trigger tasks based on action type
            switch (event.getActionType()) {
                case "REGISTRATION":
                    eventTaskService.triggerWelcomeEmailTask(event);
                    eventTaskService.triggerProfileInitializationTask(event);
                    eventTaskService.triggerAnalyticsTask(event);
                    break;
                case "LOGIN":
                    eventTaskService.triggerLoginAnalyticsTask(event);
                    break;
                case "PROFILE_UPDATE":
                    eventTaskService.triggerProfileSyncTask(event);
                    break;
                default:
                    log.info("No task mapping for action type: {}", event.getActionType());
            }
            
        } catch (Exception e) {
            log.error("Error processing user action event: {}", event.getEventId(), e);
            eventTaskService.routeToDeadLetter(event, e.getMessage());
        }
    }
    
    private boolean isValidUserEvent(UserActionEvent event) {
        return event.getUserId() != null && 
               event.getUsername() != null && 
               event.getActionType() != null;
    }
}
