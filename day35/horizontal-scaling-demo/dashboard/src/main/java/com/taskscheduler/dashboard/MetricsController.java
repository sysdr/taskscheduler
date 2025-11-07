package com.taskscheduler.dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @GetMapping
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("demoData", true);
        metrics.put("totalProduced", 240);
        metrics.put("totalCompleted", 225);
        metrics.put("queueDepth", 15);
        metrics.put("consumers", createSampleConsumers());
        return metrics;
    }

    private List<Map<String, Object>> createSampleConsumers() {
        List<Map<String, Object>> demoConsumers = new ArrayList<>();
        demoConsumers.add(createConsumerMetric("consumer-demo-1", 95, 2));
        demoConsumers.add(createConsumerMetric("consumer-demo-2", 78, 1));
        demoConsumers.add(createConsumerMetric("consumer-demo-3", 52, 0));
        return demoConsumers;
    }

    private Map<String, Object> createConsumerMetric(String id, int processed, int failed) {
        Map<String, Object> consumer = new HashMap<>();
        consumer.put("id", id);
        consumer.put("processed", processed);
        consumer.put("failed", failed);
        return consumer;
    }
}
