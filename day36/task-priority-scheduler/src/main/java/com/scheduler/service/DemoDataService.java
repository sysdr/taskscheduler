package com.scheduler.service;

import com.scheduler.dto.DashboardMetrics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DemoDataService {

    public DashboardMetrics getDemoMetrics() {
        DashboardMetrics metrics = new DashboardMetrics();

        metrics.setSubmittedCounts(Map.of(
                "HIGH", 128,
                "NORMAL", 342,
                "LOW", 216
        ));

        metrics.setQueueDepths(Map.of(
                "HIGH", 3,
                "NORMAL", 9,
                "LOW", 14
        ));

        metrics.setAvgProcessingTimes(Map.of(
                "HIGH", 420L,
                "NORMAL", 880L,
                "LOW", 1680L
        ));

        metrics.setThroughputRates(Map.of(
                "HIGH", 28.0,
                "NORMAL", 18.0,
                "LOW", 9.0
        ));

        DashboardMetrics.SystemStatus systemStatus = new DashboardMetrics.SystemStatus();
        systemStatus.setOverall("Stable");
        systemStatus.setSuccessRate(0.982);
        systemStatus.setWorkerUtilization(0.76);
        systemStatus.setBacklogRatio(0.21);
        systemStatus.setUpdatedAt("moments ago");
        metrics.setSystemStatus(systemStatus);

        metrics.setHighlights(List.of(
                "High priority SLA holding at 99.2%",
                "Backlog trimmed by 12% in the last hour",
                "Throughput up 8% after scaling worker pool"
        ));

        List<DashboardMetrics.RecentTask> recentTasks = new ArrayList<>();

        DashboardMetrics.RecentTask taskOne = new DashboardMetrics.RecentTask();
        taskOne.setName("Realtime fraud detection");
        taskOne.setPriority("HIGH");
        taskOne.setStatus("PROCESSING");
        taskOne.setSubmittedAgo("just now");
        taskOne.setProcessingTime("~18s remaining");
        recentTasks.add(taskOne);

        DashboardMetrics.RecentTask taskTwo = new DashboardMetrics.RecentTask();
        taskTwo.setName("Generate monthly invoices");
        taskTwo.setPriority("NORMAL");
        taskTwo.setStatus("QUEUED");
        taskTwo.setSubmittedAgo("3 min ago");
        taskTwo.setProcessingTime("queued behind 4 jobs");
        recentTasks.add(taskTwo);

        DashboardMetrics.RecentTask taskThree = new DashboardMetrics.RecentTask();
        taskThree.setName("Archive audit logs");
        taskThree.setPriority("LOW");
        taskThree.setStatus("COMPLETED");
        taskThree.setSubmittedAgo("12 min ago");
        taskThree.setProcessingTime("completed in 31s");
        recentTasks.add(taskThree);

        metrics.setRecentTasks(recentTasks);

        return metrics;
    }
}


