package com.taskscheduler.dto;

import com.taskscheduler.entity.TaskDefinition;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public record TaskDefinitionSearchRequest(
    TaskDefinition.TaskStatus status,
    String namePattern,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime startDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime endDate,
    Integer page,
    Integer size,
    String sortBy,
    String sortDir
) {
    public TaskDefinitionSearchRequest {
        // Default values
        if (page == null || page < 0) page = 0;
        if (size == null || size <= 0 || size > 100) size = 20;
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (sortDir == null || (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc"))) {
            sortDir = "desc";
        }
    }
}
