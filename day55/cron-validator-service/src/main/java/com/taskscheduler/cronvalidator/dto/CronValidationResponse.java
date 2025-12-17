package com.taskscheduler.cronvalidator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CronValidationResponse {
    private boolean valid;
    private String expression;
    private String humanReadable;
    private String errorMessage;
    private List<LocalDateTime> nextExecutions;
    private String timezone;
    private CronBreakdown breakdown;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CronBreakdown {
        private String minute;
        private String hour;
        private String dayOfMonth;
        private String month;
        private String dayOfWeek;
    }
}
