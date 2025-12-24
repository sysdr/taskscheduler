package com.taskscheduler.api.dto;

import lombok.Data;

@Data
public class TaskDTO {
    private Long id;
    private String name;
    private String description;
    private String cronExpression;
    private String handlerClass;
    private String status;
    private String priority;
    private String nextExecution;
    private String lastExecution;
    private Integer executionCount;
    private Integer failureCount;
    private Boolean enabled;
    private String timezone;
}
