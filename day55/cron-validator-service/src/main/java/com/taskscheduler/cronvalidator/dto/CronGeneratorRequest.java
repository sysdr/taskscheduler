package com.taskscheduler.cronvalidator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CronGeneratorRequest {
    private String type; // MINUTELY, HOURLY, DAILY, WEEKLY, MONTHLY, CUSTOM
    private String minute = "0";
    private String hour = "0";
    private String dayOfMonth = "*";
    private String month = "*";
    private String dayOfWeek = "*";
}
