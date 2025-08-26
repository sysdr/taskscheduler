package com.ultrascale.scheduler.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class SampleCalculationTask {
    
    public double performCalculation() {
        Random random = new Random();
        double result = 0;
        
        // Simulate some complex calculation
        for (int i = 0; i < 1000000; i++) {
            result += Math.sin(random.nextDouble()) * Math.cos(random.nextDouble());
        }
        
        return result;
    }
    
    public String getTaskDescription() {
        return "Performs complex mathematical calculations for demonstration purposes";
    }
}
