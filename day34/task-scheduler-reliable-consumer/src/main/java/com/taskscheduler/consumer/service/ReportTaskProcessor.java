package com.taskscheduler.consumer.service;

import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.model.ProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class ReportTaskProcessor implements TaskProcessor {
    private static final Logger log = LoggerFactory.getLogger(ReportTaskProcessor.class);
    private final Random random = new Random();

    @Override
    public ProcessingResult process(Task task) {
        long startTime = System.currentTimeMillis();
        
        try {
            String reportType = (String) task.getPayload().get("reportType");
            String dateRange = (String) task.getPayload().get("dateRange");
            
            if (reportType == null) {
                return ProcessingResult.permanentFailure(
                    "Missing report type", 
                    new IllegalArgumentException("reportType is required"),
                    System.currentTimeMillis() - startTime
                );
            }

            // Simulate longer processing for reports
            Thread.sleep(500 + random.nextInt(1000));
            
            // Simulate failure scenarios
            int scenario = random.nextInt(100);
            if (scenario < 3) { // 3% database timeout
                throw new RuntimeException("Database timeout during report generation");
            } else if (scenario < 6) { // 3% insufficient resources
                throw new RuntimeException("Insufficient memory to generate large report");
            }
            
            log.info("📊 Report generated successfully: {} for period {}", reportType, dateRange);
            return ProcessingResult.success(
                "Report " + reportType + " generated for " + dateRange, 
                System.currentTimeMillis() - startTime
            );
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ProcessingResult.retryableFailure(
                "Report generation interrupted", e, 
                System.currentTimeMillis() - startTime
            );
        } catch (Exception e) {
            return ProcessingResult.retryableFailure(
                "Report generation failed: " + e.getMessage(), e,
                System.currentTimeMillis() - startTime
            );
        }
    }

    @Override
    public boolean canProcess(String taskType) {
        return "REPORT".equalsIgnoreCase(taskType);
    }
}
