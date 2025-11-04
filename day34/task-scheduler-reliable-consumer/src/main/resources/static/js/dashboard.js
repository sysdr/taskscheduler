class ConsumerDashboard {
    constructor() {
        this.previousMetrics = {};
        this.tasksChart = null;
        this.successRateChart = null;
        this.initializeCharts();
        this.startMetricsPolling();
    }

    initializeCharts() {
        // Tasks over time chart
        const tasksCtx = document.getElementById('tasksChart').getContext('2d');
        this.tasksChart = new Chart(tasksCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [
                    {
                        label: 'Successful',
                        data: [],
                        borderColor: '#22c55e',
                        backgroundColor: 'rgba(34, 197, 94, 0.1)',
                        fill: true,
                        tension: 0.4
                    },
                    {
                        label: 'Failed',
                        data: [],
                        borderColor: '#ef4444',
                        backgroundColor: 'rgba(239, 68, 68, 0.1)',
                        fill: true,
                        tension: 0.4
                    },
                    {
                        label: 'Retried',
                        data: [],
                        borderColor: '#f59e0b',
                        backgroundColor: 'rgba(245, 158, 11, 0.1)',
                        fill: true,
                        tension: 0.4
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    x: {
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    }
                }
            }
        });

        // Success rate doughnut chart
        const successRateCtx = document.getElementById('successRateChart').getContext('2d');
        this.successRateChart = new Chart(successRateCtx, {
            type: 'doughnut',
            data: {
                labels: ['Successful', 'Failed', 'Retried'],
                datasets: [{
                    data: [0, 0, 0],
                    backgroundColor: [
                        '#22c55e',
                        '#ef4444',
                        '#f59e0b'
                    ],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    async fetchMetrics() {
        try {
            const response = await fetch('/api/metrics');
            const metrics = await response.json();
            this.updateMetrics(metrics);
            this.updateCharts(metrics);
            this.updateStatus(true);
        } catch (error) {
            console.error('Failed to fetch metrics:', error);
            this.updateStatus(false);
        }
    }

    updateMetrics(metrics) {
        // Update metric values
        document.getElementById('successfulTasks').textContent = Math.round(metrics.successfulTasks);
        document.getElementById('failedTasks').textContent = Math.round(metrics.failedTasks);
        document.getElementById('retriedTasks').textContent = Math.round(metrics.retriedTasks);
        document.getElementById('avgProcessingTime').textContent = Math.round(metrics.averageProcessingTime) + 'ms';

        // Update changes if we have previous metrics
        if (Object.keys(this.previousMetrics).length > 0) {
            this.updateChange('successfulChange', metrics.successfulTasks - this.previousMetrics.successfulTasks);
            this.updateChange('failedChange', metrics.failedTasks - this.previousMetrics.failedTasks);
            this.updateChange('retriedChange', metrics.retriedTasks - this.previousMetrics.retriedTasks);
            this.updateChange('timingChange', 
                Math.round(metrics.averageProcessingTime - this.previousMetrics.averageProcessingTime), 'ms');
        }

        this.previousMetrics = { ...metrics };
    }

    updateChange(elementId, change, suffix = '') {
        const element = document.getElementById(elementId);
        const sign = change > 0 ? '+' : '';
        element.textContent = sign + change + suffix;
        element.className = 'metric-change ' + (change > 0 ? 'positive' : change < 0 ? 'negative' : 'neutral');
    }

    updateCharts(metrics) {
        const now = new Date().toLocaleTimeString();
        const maxDataPoints = 10;

        // Update tasks chart
        if (this.tasksChart.data.labels.length >= maxDataPoints) {
            this.tasksChart.data.labels.shift();
            this.tasksChart.data.datasets.forEach(dataset => dataset.data.shift());
        }

        this.tasksChart.data.labels.push(now);
        this.tasksChart.data.datasets[0].data.push(metrics.successfulTasks);
        this.tasksChart.data.datasets[1].data.push(metrics.failedTasks);
        this.tasksChart.data.datasets[2].data.push(metrics.retriedTasks);
        this.tasksChart.update('none');

        // Update success rate chart
        this.successRateChart.data.datasets[0].data = [
            metrics.successfulTasks,
            metrics.failedTasks,
            metrics.retriedTasks
        ];
        this.successRateChart.update('none');
    }

    updateStatus(connected) {
        const statusIndicator = document.getElementById('statusIndicator');
        const statusDot = statusIndicator.querySelector('.status-dot');
        const statusText = statusIndicator.querySelector('.status-text');

        if (connected) {
            statusDot.style.background = '#22c55e';
            statusText.textContent = 'Connected';
            statusIndicator.style.background = 'rgba(34, 197, 94, 0.1)';
            statusIndicator.style.borderColor = 'rgba(34, 197, 94, 0.2)';
        } else {
            statusDot.style.background = '#ef4444';
            statusText.textContent = 'Disconnected';
            statusIndicator.style.background = 'rgba(239, 68, 68, 0.1)';
            statusIndicator.style.borderColor = 'rgba(239, 68, 68, 0.2)';
        }
    }

    addLogEntry(type, message) {
        const logsContainer = document.getElementById('logsContainer');
        const timestamp = new Date().toLocaleTimeString();
        
        const logEntry = document.createElement('div');
        logEntry.className = `log-entry ${type}`;
        logEntry.innerHTML = `
            <span class="log-timestamp">${timestamp}</span>
            <span class="log-message">${message}</span>
        `;
        
        logsContainer.insertBefore(logEntry, logsContainer.firstChild);
        
        // Keep only last 20 entries
        while (logsContainer.children.length > 20) {
            logsContainer.removeChild(logsContainer.lastChild);
        }
    }

    startMetricsPolling() {
        // Initial fetch
        this.fetchMetrics();
        
        // Poll every 2 seconds
        setInterval(() => {
            this.fetchMetrics();
        }, 2000);

        // Simulate some log entries for demonstration
        setTimeout(() => {
            this.addLogEntry('info', 'Kafka consumer connected to task-execution topic');
        }, 1000);
        
        setTimeout(() => {
            this.addLogEntry('success', 'Email task processor registered successfully');
        }, 2000);
        
        setTimeout(() => {
            this.addLogEntry('success', 'Report task processor registered successfully');
        }, 3000);
    }
}

// Initialize dashboard when page loads
document.addEventListener('DOMContentLoaded', () => {
    new ConsumerDashboard();
});
