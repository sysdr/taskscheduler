package com.taskscheduler.cronvalidator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CronGeneratorResponse {
    private String expression;
    private String humanReadable;
    private String type;
}
