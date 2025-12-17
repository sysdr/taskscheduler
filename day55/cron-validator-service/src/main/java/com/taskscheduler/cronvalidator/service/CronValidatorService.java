package com.taskscheduler.cronvalidator.service;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.taskscheduler.cronvalidator.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class CronValidatorService {
    
    private final CronDefinition cronDefinition;
    private final CronParser parser;
    private final CronDescriptor descriptor;
    
    public CronValidatorService() {
        this.cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
        this.parser = new CronParser(cronDefinition);
        this.descriptor = CronDescriptor.instance(Locale.US);
    }
    
    @Cacheable(value = "cronValidations", key = "#request.expression + '-' + #request.timezone")
    public CronValidationResponse validate(CronValidationRequest request) {
        log.debug("Validating cron expression: {}", request.getExpression());
        
        try {
            Cron cron = parser.parse(request.getExpression());
            cron.validate();
            
            String humanReadable = descriptor.describe(cron);
            List<LocalDateTime> nextExecutions = calculateNextExecutions(
                cron, 
                request.getTimezone(), 
                request.getPreviewCount()
            );
            
            CronValidationResponse.CronBreakdown breakdown = parseBreakdown(request.getExpression());
            
            return CronValidationResponse.builder()
                .valid(true)
                .expression(request.getExpression())
                .humanReadable(humanReadable)
                .nextExecutions(nextExecutions)
                .timezone(request.getTimezone())
                .breakdown(breakdown)
                .build();
                
        } catch (Exception e) {
            log.error("Validation failed for expression: {}", request.getExpression(), e);
            return CronValidationResponse.builder()
                .valid(false)
                .expression(request.getExpression())
                .errorMessage(e.getMessage())
                .timezone(request.getTimezone())
                .build();
        }
    }
    
    public CronGeneratorResponse generate(CronGeneratorRequest request) {
        log.debug("Generating cron expression for type: {}", request.getType());
        
        String expression = switch (request.getType().toUpperCase()) {
            case "MINUTELY" -> "*/1 * * * *";
            case "HOURLY" -> String.format("%s * * * *", request.getMinute());
            case "DAILY" -> String.format("%s %s * * *", request.getMinute(), request.getHour());
            case "WEEKLY" -> String.format("%s %s * * %s", 
                request.getMinute(), request.getHour(), request.getDayOfWeek());
            case "MONTHLY" -> String.format("%s %s %s * *", 
                request.getMinute(), request.getHour(), request.getDayOfMonth());
            case "CUSTOM" -> String.format("%s %s %s %s %s",
                request.getMinute(), request.getHour(), request.getDayOfMonth(),
                request.getMonth(), request.getDayOfWeek());
            default -> throw new IllegalArgumentException("Unknown type: " + request.getType());
        };
        
        try {
            Cron cron = parser.parse(expression);
            String humanReadable = descriptor.describe(cron);
            
            return CronGeneratorResponse.builder()
                .expression(expression)
                .humanReadable(humanReadable)
                .type(request.getType())
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate cron expression", e);
        }
    }
    
    private List<LocalDateTime> calculateNextExecutions(Cron cron, String timezone, int count) {
        List<LocalDateTime> executions = new ArrayList<>();
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        
        for (int i = 0; i < count; i++) {
            now = executionTime.nextExecution(now).orElse(now);
            executions.add(now.toLocalDateTime());
        }
        
        return executions;
    }
    
    private CronValidationResponse.CronBreakdown parseBreakdown(String expression) {
        String[] parts = expression.trim().split("\\s+");
        
        if (parts.length != 5) {
            return null;
        }
        
        return CronValidationResponse.CronBreakdown.builder()
            .minute(parts[0])
            .hour(parts[1])
            .dayOfMonth(parts[2])
            .month(parts[3])
            .dayOfWeek(parts[4])
            .build();
    }
    
    public boolean isValidCronExpression(String expression) {
        try {
            Cron cron = parser.parse(expression);
            cron.validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
