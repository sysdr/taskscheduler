class TaskDashboard {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8080/api';
        this.statusChart = null;
        this.refreshInterval = null;
        this.init();
    }

    init() {
        this.initChart();
        this.startAutoRefresh();
        this.updateTimestamp();
        this.loadInitialData();
        
        // Update timestamp every second
        setInterval(() => this.updateTimestamp(), 1000);
    }

    updateTimestamp() {
        const now = new Date();
        document.getElementById('currentTime').textContent = 
            now.toLocaleString('en-US', { 
                dateStyle: 'short', 
                timeStyle: 'medium' 
            });
    }

    initChart() {
        const ctx = document.getElementById('statusChart').getContext('2d');
        this.statusChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Scheduled', 'Queued', 'Processing', 'Completed', 'Failed'],
                datasets: [{
                    data: [0, 0, 0, 0, 0],
                    backgroundColor: [
                        '#fd7e14',
                        '#6f42c1', 
                        '#007bff',
                        '#28a745',
                        '#dc3545'
                    ],
                    borderWidth: 0,
                    cutout: '70%'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {
                            usePointStyle: true,
                            padding: 20,
                            font: {
                                size: 12,
                                weight: '600'
                            }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const label = context.label;
                                const value = context.parsed;
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                                return `${label}: ${value} (${percentage}%)`;
                            }
                        }
                    }
                }
            }
        });
    }

    async loadInitialData() {
        await this.refreshData();
    }

    startAutoRefresh() {
        this.refreshInterval = setInterval(() => {
            this.refreshData();
        }, 3000);
    }

    async refreshData() {
        try {
            await Promise.all([
                this.updateStats(),
                this.updateTasksList(),
                this.updateExecutionsList()
            ]);
            
            document.getElementById('systemStatus').className = 'status-indicator active';
            document.getElementById('systemStatus').innerHTML = '<span class="status-dot"></span>System Online';
        } catch (error) {
            console.error('Failed to refresh data:', error);
            document.getElementById('systemStatus').className = 'status-indicator offline';
            document.getElementById('systemStatus').innerHTML = '<span class="status-dot" style="background: #dc3545;"></span>Connection Error';
        }
    }

    async updateStats() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/tasks/stats`);
            if (!response.ok) throw new Error('Failed to fetch stats');
            
            const stats = await response.json();
            
            // Update stat cards
            document.getElementById('scheduledCount').textContent = stats.scheduled || 0;
            document.getElementById('queuedCount').textContent = stats.queued || 0;
            document.getElementById('processingCount').textContent = stats.processing || 0;
            document.getElementById('completedCount').textContent = stats.completed || 0;
            document.getElementById('failedCount').textContent = stats.failed || 0;
            
            // Update chart
            if (this.statusChart) {
                this.statusChart.data.datasets[0].data = [
                    stats.scheduled || 0,
                    stats.queued || 0,
                    stats.processing || 0,
                    stats.completed || 0,
                    stats.failed || 0
                ];
                this.statusChart.update();
            }
        } catch (error) {
            console.error('Failed to update stats:', error);
        }
    }

    async updateTasksList() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/tasks`);
            if (!response.ok) throw new Error('Failed to fetch tasks');
            
            const tasks = await response.json();
            const tbody = document.getElementById('tasksTableBody');
            
            tbody.innerHTML = tasks
                .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
                .slice(0, 20) // Show last 20 tasks
                .map(task => `
                    <tr>
                        <td><code>${this.truncateId(task.taskId)}</code></td>
                        <td>${task.taskName || 'N/A'}</td>
                        <td>${task.taskType || 'N/A'}</td>
                        <td><span class="status-badge status-${task.status.toLowerCase()}">${task.status}</span></td>
                        <td><span class="priority-badge priority-${this.getPriorityClass(task.priority)}">${task.priority || 5}</span></td>
                        <td>${this.formatDateTime(task.createdAt)}</td>
                        <td>${this.formatDateTime(task.scheduledAt)}</td>
                    </tr>
                `).join('');
        } catch (error) {
            console.error('Failed to update tasks list:', error);
        }
    }

    async updateExecutionsList() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/tasks/executions`);
            if (!response.ok) throw new Error('Failed to fetch executions');
            
            const executions = await response.json();
            const tbody = document.getElementById('executionsTableBody');
            
            tbody.innerHTML = executions
                .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
                .slice(0, 20)
                .map(exec => `
                    <tr>
                        <td><code>${this.truncateId(exec.executionId)}</code></td>
                        <td><code>${this.truncateId(exec.taskId)}</code></td>
                        <td>${exec.workerId || 'N/A'}</td>
                        <td><span class="status-badge status-${exec.status.toLowerCase()}">${exec.status}</span></td>
                        <td>${this.formatDateTime(exec.startTime)}</td>
                        <td>${exec.executionTimeMs ? exec.executionTimeMs + 'ms' : 'N/A'}</td>
                        <td title="${exec.result || exec.errorMessage || 'N/A'}">${this.truncateText(exec.result || exec.errorMessage || 'N/A', 50)}</td>
                    </tr>
                `).join('');
        } catch (error) {
            console.error('Failed to update executions list:', error);
        }
    }

    async createSampleTasks() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/demo/create-sample-tasks`, {
                method: 'POST'
            });
            
            if (!response.ok) throw new Error('Failed to create sample tasks');
            
            const result = await response.json();
            
            // Show success message
            this.showNotification('Sample tasks created successfully!', 'success');
            
            // Refresh data immediately
            setTimeout(() => this.refreshData(), 1000);
            
        } catch (error) {
            console.error('Failed to create sample tasks:', error);
            this.showNotification('Failed to create sample tasks. Please check if Kafka is running.', 'error');
        }
    }

    showNotification(message, type) {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        
        // Style the notification
        Object.assign(notification.style, {
            position: 'fixed',
            top: '20px',
            right: '20px',
            padding: '15px 25px',
            borderRadius: '12px',
            color: 'white',
            fontWeight: '600',
            zIndex: '9999',
            backgroundColor: type === 'success' ? '#28a745' : '#dc3545',
            boxShadow: '0 5px 15px rgba(0, 0, 0, 0.3)',
            transform: 'translateX(100%)',
            transition: 'transform 0.3s ease'
        });
        
        document.body.appendChild(notification);
        
        // Animate in
        setTimeout(() => {
            notification.style.transform = 'translateX(0)';
        }, 100);
        
        // Remove after 3 seconds
        setTimeout(() => {
            notification.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    }

    truncateId(id) {
        if (!id) return 'N/A';
        return id.length > 8 ? id.substring(0, 8) + '...' : id;
    }

    truncateText(text, maxLength) {
        if (!text) return 'N/A';
        return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    }

    getPriorityClass(priority) {
        if (priority >= 8) return 'high';
        if (priority >= 5) return 'medium';
        return 'low';
    }

    formatDateTime(dateTimeStr) {
        if (!dateTimeStr) return 'N/A';
        const date = new Date(dateTimeStr);
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString('en-US', { 
            hour12: false,
            hour: '2-digit',
            minute: '2-digit'
        });
    }
}

// Global functions for button clicks
function createSampleTasks() {
    window.dashboard.createSampleTasks();
}

function refreshData() {
    window.dashboard.refreshData();
}

function showTasks() {
    document.getElementById('tasksView').style.display = 'block';
    document.getElementById('executionsView').style.display = 'none';
    
    document.querySelectorAll('.view-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelector('.view-btn').classList.add('active');
}

function showExecutions() {
    document.getElementById('tasksView').style.display = 'none';
    document.getElementById('executionsView').style.display = 'block';
    
    document.querySelectorAll('.view-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.view-btn')[1].classList.add('active');
}

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.dashboard = new TaskDashboard();
});
