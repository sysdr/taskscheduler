package com.taskscheduler.alerting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    private String id;
    private String name;
    private AlertSeverity severity;
    private AlertStatus status;
    private String description;
    private String metric;
    private Double currentValue;
    private Double threshold;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private Map<String, String> labels;
    private String runbookUrl;
    private String dashboardUrl;
}
