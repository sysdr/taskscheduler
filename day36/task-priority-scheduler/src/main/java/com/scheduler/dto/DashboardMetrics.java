package com.scheduler.dto;

import java.util.List;
import java.util.Map;

public class DashboardMetrics {

    private Map<String, Integer> submittedCounts;
    private Map<String, Integer> queueDepths;
    private Map<String, Long> avgProcessingTimes;
    private Map<String, Double> throughputRates;
    private SystemStatus systemStatus;
    private List<String> highlights;
    private List<RecentTask> recentTasks;

    public Map<String, Integer> getSubmittedCounts() {
        return submittedCounts;
    }

    public void setSubmittedCounts(Map<String, Integer> submittedCounts) {
        this.submittedCounts = submittedCounts;
    }

    public Map<String, Integer> getQueueDepths() {
        return queueDepths;
    }

    public void setQueueDepths(Map<String, Integer> queueDepths) {
        this.queueDepths = queueDepths;
    }

    public Map<String, Long> getAvgProcessingTimes() {
        return avgProcessingTimes;
    }

    public void setAvgProcessingTimes(Map<String, Long> avgProcessingTimes) {
        this.avgProcessingTimes = avgProcessingTimes;
    }

    public Map<String, Double> getThroughputRates() {
        return throughputRates;
    }

    public void setThroughputRates(Map<String, Double> throughputRates) {
        this.throughputRates = throughputRates;
    }

    public SystemStatus getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(SystemStatus systemStatus) {
        this.systemStatus = systemStatus;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public List<RecentTask> getRecentTasks() {
        return recentTasks;
    }

    public void setRecentTasks(List<RecentTask> recentTasks) {
        this.recentTasks = recentTasks;
    }

    public static class SystemStatus {
        private String overall;
        private double successRate;
        private double workerUtilization;
        private double backlogRatio;
        private String updatedAt;

        public String getOverall() {
            return overall;
        }

        public void setOverall(String overall) {
            this.overall = overall;
        }

        public double getSuccessRate() {
            return successRate;
        }

        public void setSuccessRate(double successRate) {
            this.successRate = successRate;
        }

        public double getWorkerUtilization() {
            return workerUtilization;
        }

        public void setWorkerUtilization(double workerUtilization) {
            this.workerUtilization = workerUtilization;
        }

        public double getBacklogRatio() {
            return backlogRatio;
        }

        public void setBacklogRatio(double backlogRatio) {
            this.backlogRatio = backlogRatio;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class RecentTask {
        private String name;
        private String priority;
        private String status;
        private String submittedAgo;
        private String processingTime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubmittedAgo() {
            return submittedAgo;
        }

        public void setSubmittedAgo(String submittedAgo) {
            this.submittedAgo = submittedAgo;
        }

        public String getProcessingTime() {
            return processingTime;
        }

        public void setProcessingTime(String processingTime) {
            this.processingTime = processingTime;
        }
    }
}


