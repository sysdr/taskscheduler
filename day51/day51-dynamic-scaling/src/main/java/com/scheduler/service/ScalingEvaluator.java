package com.scheduler.service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
public class ScalingEvaluator {
    private final AutoScalingService scaler;
    public ScalingEvaluator(AutoScalingService scaler) { this.scaler = scaler; }
    @Scheduled(fixedDelay = 10000)
    public void evaluate() { scaler.evaluate(); }
}
