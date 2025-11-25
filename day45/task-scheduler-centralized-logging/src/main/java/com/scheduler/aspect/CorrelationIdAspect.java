package com.scheduler.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class CorrelationIdAspect {

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled) || " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    public Object addCorrelationId(ProceedingJoinPoint joinPoint) throws Throwable {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);
        
        try {
            return joinPoint.proceed();
        } finally {
            MDC.remove("correlation_id");
        }
    }
}
