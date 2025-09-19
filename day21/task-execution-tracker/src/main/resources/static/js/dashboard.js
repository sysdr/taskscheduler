// Dashboard JavaScript for Task Execution Tracker
class TaskDashboard {
    constructor() {
        this.statusChart = null;
        this.timelineChart = null;
        this.init();
        this.startAutoRefresh();
    }
    
    init() {
        this.loadDashboardData();
        this.initCharts();
    }
    
    async loadDashboardData() {
        try {
            await Promise.all([
                this.loadStats(),
                this.loadFailedTasks(),
                this.loadLongestTasks(),
                this.loadRecentActivity()
            ]);
            this.updateLastRefreshTime();
        } catch (error) {
            console.error('Error loading dashboard data:', error);
        }
    }
    
    async loadStats() {
        const response = await fetch('/api/executions/stats');
        const stats = await response.json();
        
        const statsCards = [
            { title: 'Pending', value: stats.pendingCount, icon: 'fas fa-clock', color: 'blue' },
            { title: 'Running', value: stats.runningCount, icon: 'fas fa-play', color: 'yellow' },
            { title: 'Success', value: stats.successCount, icon: 'fas fa-check', color: 'green' },
            { title: 'Failed', value: stats.failedCount, icon: 'fas fa-times', color: 'red' },
            { 
                title: 'Avg Duration', 
                value: stats.averageExecutionTimeMs ? Math.round(stats.averageExecutionTimeMs) + 'ms' : '0ms', 
                icon: 'fas fa-stopwatch', 
                color: 'purple' 
            }
        ];
        
        this.renderStatsCards(statsCards);
        this.updateStatusChart(stats);
        this.updatePerformanceMetrics(stats);
    }
    
    renderStatsCards(cards) {
        const container = document.getElementById('stats-cards');
        container.innerHTML = cards.map(card => `
            <div class="bg-white rounded-lg shadow-lg p-6 card-hover border-l-4 border-${card.color}-500">
                <div class="flex items-center">
                    <div class="flex-shrink-0">
                        <i class="${card.icon} text-2xl text-${card.color}-600"></i>
                    </div>
                    <div class="ml-4">
                        <p class="text-sm font-medium text-gray-600">${card.title}</p>
                        <p class="text-2xl font-bold text-gray-900">${card.value}</p>
                    </div>
                </div>
            </div>
        `).join('');
    }
    
    async loadFailedTasks() {
        const response = await fetch('/api/executions/failed?page=0&size=5');
        const data = await response.json();
        
        const tableBody = document.getElementById('failed-tasks-table');
        const failedCount = document.getElementById('failed-count');
        
        if (data.content && data.content.length > 0) {
            failedCount.textContent = data.content.length;
            tableBody.innerHTML = data.content.map(task => `
                <tr class="hover:bg-gray-50">
                    <td class="px-4 py-3 text-sm text-gray-900 font-medium">${task.taskName}</td>
                    <td class="px-4 py-3 text-sm text-red-600" title="${task.errorMessage}">
                        ${this.truncateText(task.errorMessage, 30)}
                    </td>
                    <td class="px-4 py-3 text-sm text-gray-500">
                        ${this.formatDateTime(task.startTime)}
                    </td>
                </tr>
            `).join('');
        } else {
            failedCount.textContent = '0';
            tableBody.innerHTML = '<tr><td colspan="3" class="px-4 py-3 text-center text-gray-500">No failed tasks</td></tr>';
        }
    }
    
    async loadLongestTasks() {
        const response = await fetch('/api/executions/longest-running?page=0&size=5');
        const data = await response.json();
        
        const tableBody = document.getElementById('longest-tasks-table');
        const longestCount = document.getElementById('longest-count');
        
        if (data.content && data.content.length > 0) {
            longestCount.textContent = data.content.length;
            tableBody.innerHTML = data.content.map(task => `
                <tr class="hover:bg-gray-50">
                    <td class="px-4 py-3 text-sm text-gray-900 font-medium">${task.taskName}</td>
                    <td class="px-4 py-3 text-sm text-gray-700">
                        ${this.formatDuration(task.durationMs)}
                    </td>
                    <td class="px-4 py-3">
                        <span class="px-2 py-1 text-xs font-semibold rounded-full ${this.getStatusBadgeClass(task.status)}">
                            ${task.status}
                        </span>
                    </td>
                </tr>
            `).join('');
        } else {
            longestCount.textContent = '0';
            tableBody.innerHTML = '<tr><td colspan="3" class="px-4 py-3 text-center text-gray-500">No completed tasks</td></tr>';
        }
    }
    
    async loadRecentActivity() {
        const response = await fetch('/api/executions/recent');
        const tasks = await response.json();
        
        // Process data for timeline chart
        const hourlyData = this.processTimelineData(tasks);
        this.updateTimelineChart(hourlyData);
    }
    
    processTimelineData(tasks) {
        const hours = {};
        const now = new Date();
        
        // Initialize last 12 hours
        for (let i = 11; i >= 0; i--) {
            const hour = new Date(now.getTime() - (i * 60 * 60 * 1000));
            const key = hour.toISOString().slice(0, 13);
            hours[key] = { success: 0, failed: 0 };
        }
        
        // Count tasks by hour
        tasks.forEach(task => {
            const taskHour = task.startTime.slice(0, 13);
            if (hours[taskHour]) {
                if (task.status === 'SUCCESS') {
                    hours[taskHour].success++;
                } else if (task.status === 'FAILED') {
                    hours[taskHour].failed++;
                }
            }
        });
        
        return hours;
    }
    
    initCharts() {
        // Status Distribution Chart
        const statusCtx = document.getElementById('statusChart').getContext('2d');
        this.statusChart = new Chart(statusCtx, {
            type: 'doughnut',
            data: {
                labels: ['Pending', 'Running', 'Success', 'Failed'],
                datasets: [{
                    data: [0, 0, 0, 0],
                    backgroundColor: [
                        '#3B82F6', // blue
                        '#EAB308', // yellow
                        '#22C55E', // green
                        '#EF4444'  // red
                    ]
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
        
        // Timeline Chart
        const timelineCtx = document.getElementById('timelineChart').getContext('2d');
        this.timelineChart = new Chart(timelineCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [
                    {
                        label: 'Success',
                        data: [],
                        borderColor: '#22C55E',
                        backgroundColor: 'rgba(34, 197, 94, 0.1)',
                        fill: true
                    },
                    {
                        label: 'Failed',
                        data: [],
                        borderColor: '#EF4444',
                        backgroundColor: 'rgba(239, 68, 68, 0.1)',
                        fill: true
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }
    
    updateStatusChart(stats) {
        this.statusChart.data.datasets[0].data = [
            stats.pendingCount,
            stats.runningCount,
            stats.successCount,
            stats.failedCount
        ];
        this.statusChart.update();
    }
    
    updateTimelineChart(hourlyData) {
        const labels = [];
        const successData = [];
        const failedData = [];
        
        Object.entries(hourlyData).forEach(([hour, counts]) => {
            const date = new Date(hour + ':00:00');
            labels.push(date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }));
            successData.push(counts.success);
            failedData.push(counts.failed);
        });
        
        this.timelineChart.data.labels = labels;
        this.timelineChart.data.datasets[0].data = successData;
        this.timelineChart.data.datasets[1].data = failedData;
        this.timelineChart.update();
    }
    
    startAutoRefresh() {
        setInterval(() => {
            this.loadDashboardData();
        }, 5000); // Refresh every 5 seconds
    }
    
    updateLastRefreshTime() {
        document.getElementById('last-updated').textContent = new Date().toLocaleTimeString();
        document.getElementById('current-time').textContent = new Date().toLocaleTimeString();
    }
    
    updatePerformanceMetrics(stats) {
        // Update success rate
        const successRate = Math.round(stats.successRate || 0);
        document.getElementById('success-rate').textContent = successRate + '%';
        document.getElementById('success-rate-bar').style.width = successRate + '%';
        
        // Update average execution time
        const avgTime = Math.round(stats.averageExecutionTimeMs || 0);
        document.getElementById('avg-time').textContent = avgTime + 'ms';
        
        // Update system health
        const healthStatus = stats.failedCount > 10 ? 'Warning' : 'Healthy';
        const healthColor = stats.failedCount > 10 ? 'text-yellow-600' : 'text-green-600';
        document.getElementById('system-health').textContent = healthStatus;
        document.getElementById('system-health').className = 'text-4xl font-bold ' + healthColor + ' mb-2';
        
        // Update task management counts
        document.getElementById('running-count').textContent = stats.runningCount || 0;
        document.getElementById('pending-count').textContent = stats.pendingCount || 0;
        document.getElementById('completed-today').textContent = stats.successCount || 0;
    }
    
    // Utility functions
    truncateText(text, length) {
        return text && text.length > length ? text.substring(0, length) + '...' : text || '';
    }
    
    formatDateTime(dateTime) {
        return new Date(dateTime).toLocaleString();
    }
    
    formatDuration(ms) {
        if (!ms) return 'N/A';
        if (ms < 1000) return ms + 'ms';
        return Math.round(ms / 1000) + 's';
    }
    
    getStatusBadgeClass(status) {
        const classes = {
            'PENDING': 'bg-blue-100 text-blue-800',
            'RUNNING': 'bg-yellow-100 text-yellow-800',
            'SUCCESS': 'bg-green-100 text-green-800',
            'FAILED': 'bg-red-100 text-red-800'
        };
        return classes[status] || 'bg-gray-100 text-gray-800';
    }
}

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new TaskDashboard();
});
