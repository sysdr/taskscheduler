package com.scheduler.timezone.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.TextStyle;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TimeZoneService {
    
    public ZonedDateTime calculateNextExecution(
            String cronExpression, 
            ZoneId zoneId, 
            ZonedDateTime after) {
        
        // Parse cron for hour and minute (simplified)
        String[] parts = cronExpression.split(" ");
        int minute = Integer.parseInt(parts[0]);
        int hour = Integer.parseInt(parts[1]);
        
        ZonedDateTime candidate = after
                .withZoneSameInstant(zoneId)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
        
        if (!candidate.isAfter(after)) {
            candidate = candidate.plusDays(1);
        }
        
        // Check for DST transition
        if (isDSTGap(candidate)) {
            candidate = adjustForGap(candidate);
            log.warn("DST gap detected at {}. Adjusted to {}", 
                    candidate.minusHours(1), candidate);
        }
        
        return candidate;
    }
    
    public boolean isDSTGap(ZonedDateTime dateTime) {
        ZoneRules rules = dateTime.getZone().getRules();
        ZoneOffsetTransition transition = rules.getTransition(dateTime.toLocalDateTime());
        return transition != null && transition.isGap();
    }
    
    public ZonedDateTime adjustForGap(ZonedDateTime dateTime) {
        ZoneRules rules = dateTime.getZone().getRules();
        ZoneOffsetTransition transition = rules.getTransition(dateTime.toLocalDateTime());
        
        if (transition != null && transition.isGap()) {
            // Move forward to first valid time after gap
            Duration gapSize = transition.getDuration();
            return dateTime.plus(gapSize);
        }
        
        return dateTime;
    }
    
    public boolean isDSTActive(ZoneId zoneId, Instant instant) {
        ZonedDateTime zdt = instant.atZone(zoneId);
        return zoneId.getRules().isDaylightSavings(instant);
    }
    
    public String getUTCOffset(ZoneId zoneId, Instant instant) {
        ZonedDateTime zdt = instant.atZone(zoneId);
        return zdt.getOffset().getId();
    }
    
    public Optional<String> getDSTWarning(ZoneId zoneId, Instant nextRun) {
        ZonedDateTime zdt = nextRun.atZone(zoneId);
        ZoneRules rules = zoneId.getRules();
        
        // Check for transitions within 7 days
        ZonedDateTime weekFromNow = zdt.plusDays(7);
        ZoneOffsetTransition nextTransition = rules.nextTransition(nextRun);
        
        if (nextTransition != null && 
            nextTransition.getInstant().isBefore(weekFromNow.toInstant())) {
            
            if (nextTransition.isGap()) {
                return Optional.of("DST Spring Forward in " + 
                        Duration.between(Instant.now(), nextTransition.getInstant()).toDays() + 
                        " days. Task time will shift forward.");
            } else if (nextTransition.isOverlap()) {
                return Optional.of("DST Fall Back in " + 
                        Duration.between(Instant.now(), nextTransition.getInstant()).toDays() + 
                        " days. Task will run at first occurrence.");
            }
        }
        
        return Optional.empty();
    }
    
    public List<String> getAllTimeZones() {
        return ZoneId.getAvailableZoneIds().stream()
                .sorted()
                .collect(Collectors.toList());
    }
    
    public Map<String, String> getTimeZoneInfo(String timeZoneId) {
        ZoneId zone = ZoneId.of(timeZoneId);
        Instant now = Instant.now();
        ZonedDateTime zdt = now.atZone(zone);
        
        Map<String, String> info = new HashMap<>();
        info.put("id", timeZoneId);
        info.put("displayName", zone.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        info.put("offset", zdt.getOffset().getId());
        info.put("dstActive", String.valueOf(isDSTActive(zone, now)));
        
        return info;
    }
}
