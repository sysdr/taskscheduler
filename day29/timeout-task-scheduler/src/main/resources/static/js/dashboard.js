class TimeoutTaskDashboard {
    constructor() {
        this.stompClient = null;
        this.tasks = new Map();
        this.stats = {
            active: 0,
            completed: 0,
            timedOut: 0,
            total: 0
        };
        this.chart = null;
        
        this.initializeWebSocket();
        this.initializeEventListeners();
        this.initializeChart();
        this.startPeriodicUpdates();
    }

    initializeWebSocket() {
        try {
            // Check if libraries are available
            if (typeof SockJS === 'undefined') {
                this.addLogEntry('SockJS library not loaded', 'error');
                return;
            }
            if (typeof Stomp === 'undefined') {
                this.addLogEntry('STOMP library not loaded', 'error');
                return;
            }

            const socket = new SockJS('/ws');
            this.stompClient = Stomp.over(socket);
            
            // Disable debug logging
            this.stompClient.debug = null;
            
            // Set connection options
            const connectOptions = {
                timeout: 10000,
                heartbeat: {
                    incoming: 10000,
                    outgoing: 10000
                }
            };
            
            this.stompClient.connect(connectOptions, (frame) => {
                console.log('Connected: ' + frame);
                this.addLogEntry('WebSocket connected successfully', 'success');
                
                this.stompClient.subscribe('/topic/task-updates', (message) => {
                    try {
                        const taskUpdate = JSON.parse(message.body);
                        this.handleTaskUpdate(taskUpdate);
                    } catch (error) {
                        console.error('Error parsing WebSocket message:', error);
                    }
                });
            }, (error) => {
                console.error('WebSocket connection error:', error);
                this.addLogEntry('WebSocket connection failed: ' + error, 'error');
                
                // Retry connection after 5 seconds
                setTimeout(() => {
                    this.addLogEntry('Retrying WebSocket connection...', 'info');
                    this.initializeWebSocket();
                }, 5000);
            });
        } catch (error) {
            console.error('Error initializing WebSocket:', error);
            this.addLogEntry('Error initializing WebSocket: ' + error.message, 'error');
            
            // Fallback to polling if WebSocket fails
            this.addLogEntry('Falling back to polling mode', 'warning');
            this.startPollingMode();
        }
    }

    startPollingMode() {
        // Fallback to polling every 2 seconds if WebSocket fails
        setInterval(() => {
            this.refreshTasks();
        }, 2000);
        this.addLogEntry('Polling mode activated - refreshing every 2 seconds', 'info');
    }

    initializeEventListeners() {
        document.getElementById('submitTask').addEventListener('click', () => {
            this.submitTask();
        });

        // Allow Enter key to submit
        document.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.submitTask();
            }
        });
    }

    initializeChart() {
        const ctx = document.getElementById('statusChart').getContext('2d');
        this.chart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Running', 'Completed', 'Timed Out', 'Cancelled'],
                datasets: [{
                    data: [0, 0, 0, 0],
                    backgroundColor: [
                        '#4299e1',  // Blue for Running
                        '#48bb78',  // Green for Completed
                        '#f56565',  // Red for Timed Out
                        '#a0aec0'   // Gray for Cancelled
                    ],
                    borderWidth: 3,
                    borderColor: '#ffffff',
                    hoverBorderWidth: 4,
                    hoverBorderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '60%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            usePointStyle: true,
                            padding: 20,
                            font: {
                                size: 14,
                                weight: 'bold'
                            }
                        }
                    },
                    title: {
                        display: true,
                        text: 'Task Status Distribution',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const label = context.label || '';
                                const value = context.parsed;
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                                return `${label}: ${value} (${percentage}%)`;
                            }
                        }
                    }
                },
                elements: {
                    arc: {
                        borderWidth: 3
                    }
                }
            }
        });
    }

    async submitTask() {
        const taskType = document.getElementById('taskType').value;
        const timeoutSeconds = parseInt(document.getElementById('timeoutSeconds').value);
        const payload = document.getElementById('payload').value || `${taskType} - ${Date.now()}`;

        try {
            const response = await fetch('/api/tasks/submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    taskType,
                    timeoutSeconds,
                    payload
                })
            });

            const result = await response.json();
            
            if (response.ok) {
                this.addLogEntry(`Task submitted: ${result.taskId}`, 'info');
                document.getElementById('payload').value = '';
            } else {
                this.addLogEntry(`Error submitting task: ${result.error}`, 'error');
            }
        } catch (error) {
            this.addLogEntry(`Network error: ${error.message}`, 'error');
        }
    }

    handleTaskUpdate(update) {
        this.tasks.set(update.taskId, update);
        this.updateTaskDisplay(update);
        this.updateStats();
        this.addLogEntry(`${update.taskId}: ${update.message}`, this.getLogLevel(update.status));
    }

    updateTaskDisplay(update) {
        const container = document.getElementById('tasksContainer');
        let taskElement = document.getElementById(`task-${update.taskId}`);

        if (!taskElement) {
            taskElement = document.createElement('div');
            taskElement.className = 'task-item';
            taskElement.id = `task-${update.taskId}`;
            container.prepend(taskElement);
        }

        const progressPercent = Math.min(100, (update.elapsedSeconds / update.timeoutSeconds) * 100);
        
        taskElement.className = `task-item ${update.status.toLowerCase()}`;
        taskElement.innerHTML = `
            <div class="task-header">
                <span class="task-id">${update.taskId.substring(0, 8)}...</span>
                <span class="task-status ${update.status.toLowerCase()}">${update.status}</span>
            </div>
            <div class="task-details">
                <strong>${update.taskType}</strong> - ${update.message}
            </div>
            <div class="task-progress">
                <div class="task-progress-bar" style="width: ${progressPercent}%"></div>
            </div>
            <div style="font-size: 12px; color: #718096; margin-top: 5px;">
                ${update.elapsedSeconds}s / ${update.timeoutSeconds}s (${progressPercent.toFixed(1)}%)
            </div>
        `;
    }

    updateStats() {
        const statusCounts = {
            running: 0,
            completed: 0,
            timedOut: 0,
            cancelled: 0,
            warning: 0
        };

        this.tasks.forEach(task => {
            const status = task.status.toLowerCase().replace('_', '');
            if (statusCounts.hasOwnProperty(status)) {
                statusCounts[status]++;
            }
        });

        const total = this.tasks.size;
        const active = statusCounts.running + statusCounts.warning;
        const successful = statusCounts.completed;
        const failed = statusCounts.timedOut;
        const successRate = total > 0 ? ((successful / total) * 100).toFixed(1) : 100;

        document.getElementById('activeTasks').textContent = active;
        document.getElementById('completedTasks').textContent = successful;
        document.getElementById('timedOutTasks').textContent = failed;
        document.getElementById('successRate').textContent = `${successRate}%`;

        // Update chart with enhanced visual representation
        const chartData = [
            statusCounts.running + statusCounts.warning,
            statusCounts.completed,
            statusCounts.timedOut,
            statusCounts.cancelled
        ];
        
        // Ensure minimum visual representation for completed and cancelled tasks
        const enhancedData = chartData.map((value, index) => {
            // For completed (index 1) and cancelled (index 3), ensure minimum visual presence
            if ((index === 1 && value > 0) || (index === 3 && value > 0)) {
                return Math.max(value, 1); // Ensure at least 1 for visual representation
            }
            return value;
        });
        
        this.chart.data.datasets[0].data = enhancedData;
        this.chart.update('active');
    }

    addLogEntry(message, level) {
        const container = document.getElementById('logContainer');
        const entry = document.createElement('div');
        entry.className = `log-entry ${level}`;
        
        const timestamp = new Date().toLocaleTimeString();
        entry.textContent = `[${timestamp}] ${message}`;
        
        container.prepend(entry);
        
        // Keep only last 100 entries
        while (container.children.length > 100) {
            container.removeChild(container.lastChild);
        }
    }

    getLogLevel(status) {
        switch (status.toLowerCase()) {
            case 'completed': return 'success';
            case 'timed_out': case 'failed': return 'error';
            case 'warning': return 'warning';
            default: return 'info';
        }
    }

    startPeriodicUpdates() {
        // Refresh task list every 5 seconds
        setInterval(() => {
            this.refreshTasks();
        }, 5000);
    }

    async refreshTasks() {
        try {
            const response = await fetch('/api/tasks');
            if (response.ok) {
                const tasks = await response.json();
                tasks.forEach(task => {
                    const update = {
                        taskId: task.taskId,
                        taskType: task.taskType,
                        status: task.status,
                        message: `Status: ${task.status}`,
                        elapsedSeconds: Math.floor(task.elapsedTime.seconds || 0),
                        timeoutSeconds: Math.floor(task.timeout.seconds || 30)
                    };
                    this.tasks.set(task.taskId, update);
                });
                this.updateStats();
            }
        } catch (error) {
            console.error('Error refreshing tasks:', error);
        }
    }
}

// Initialize dashboard when page loads
document.addEventListener('DOMContentLoaded', () => {
    new TimeoutTaskDashboard();
});
