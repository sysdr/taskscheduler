package com.taskscheduler.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.model.FileUploadEvent;
import com.taskscheduler.service.EventTaskService;
import com.taskscheduler.service.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileUploadEventListener {
    
    private final EventTaskService eventTaskService;
    private final MetricsService metricsService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "file-events", groupId = "task-scheduler-group")
    public void handleFileUploadEvent(
            @Payload FileUploadEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        
        try {
            log.info("Received file upload event: {} from bucket: {}", 
                    event.getFileName(), event.getBucketName());
            
            metricsService.incrementFileEvents();
            
            // Validate event
            if (!isValidFileEvent(event)) {
                log.warn("Invalid file upload event: {}", event.getEventId());
                eventTaskService.routeToDeadLetter(event, "Invalid event structure");
                return;
            }
            
            // Trigger appropriate tasks based on file type
            if (event.getFileType().startsWith("image/")) {
                eventTaskService.triggerImageProcessingTask(event);
            } else if (event.getFileType().equals("application/pdf")) {
                eventTaskService.triggerPdfProcessingTask(event);
            } else {
                eventTaskService.triggerGenericFileProcessingTask(event);
            }
            
        } catch (Exception e) {
            log.error("Error processing file upload event: {}", event.getEventId(), e);
            eventTaskService.routeToDeadLetter(event, e.getMessage());
        }
    }
    
    private boolean isValidFileEvent(FileUploadEvent event) {
        return event.getBucketName() != null && 
               event.getFileName() != null && 
               event.getFileType() != null &&
               event.getFileSize() > 0;
    }
}
