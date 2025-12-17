package com.taskscheduler.cronvalidator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CronValidationRequest {
    @NotBlank(message = "Cron expression cannot be empty")
    private String expression;
    
    private String timezone = "UTC";
    
    private Integer previewCount = 5;
}
