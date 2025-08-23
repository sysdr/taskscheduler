class TaskSchedulerDashboard {
    constructor() {
        this.baseUrl = '/api';
        this.init();
        this.setupEventListeners();
        this.startLiveUpdates();
    }

    init() {
        this.loadTasks();
        this.loadSystemStatus();
        this.logActivity('System', 'Dashboard initialized', 'info');
    }

    setupEventListeners() {
        // Task form submission
        document.getElementById('taskForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.createTask();
        });

        // Refresh button
        document.getElementById('refreshBtn').addEventListener('click', () => {
            this.refreshAll();
        });

        // Reload scheduler button
        document.getElementById('reloadSchedulerBtn').addEventListener('click', () => {
            this.reloadScheduler();
        });

        // Clear logs button
        document.getElementById('clearLogsBtn').addEventListener('click', () => {
            this.clearLogs();
        });
    }

    async createTask() {
        const formData = {
            taskName: document.getElementById('taskName').value,
            description: document.getElementById('description').value,
            cronExpression: document.getElementById('cronExpression').value,
            taskType: document.getElementById('taskType').value,
            taskData: document.getElementById('taskData').value,
            status: document.getElementById('status').value
        };

        try {
            const response = await fetch(`${this.baseUrl}/tasks`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                const task = await response.json();
                this.logActivity('Task Created', `Task '${task.taskName}' created successfully`, 'success');
                document.getElementById('taskForm').reset();
                this.loadTasks();
                this.loadSystemStatus();
            } else {
                throw new Error('Failed to create task');
            }
        } catch (error) {
            this.logActivity('Error', `Failed to create task: ${error.message}`, 'error');
        }
    }

    async loadTasks() {
        try {
            const response = await fetch(`${this.baseUrl}/tasks`);
            const tasks = await response.json();
            this.renderTasks(tasks);
            this.updateTaskCount(tasks.length);
        } catch (error) {
            this.logActivity('Error', `Failed to load tasks: ${error.message}`, 'error');
        }
    }

    renderTasks(tasks) {
        const tasksList = document.getElementById('tasksList');
        
        if (tasks.length === 0) {
            tasksList.innerHTML = '<p style="text-align: center; color: #7f8c8d;">No tasks found. Create your first task!</p>';
            return;
        }

        tasksList.innerHTML = tasks.map(task => `
            <div class="task-item ${task.status.toLowerCase()}">
                <div class="task-header">
                    <span class="task-name">${task.taskName}</span>
                    <span class="task-status ${task.status.toLowerCase()}">${task.status}</span>
                </div>
                <div class="task-details">
                    <p><strong>Type:</strong> ${task.taskType}</p>
                    <p><strong>Cron:</strong> ${task.cronExpression}</p>
                    <p><strong>Description:</strong> ${task.description}</p>
                    ${task.nextExecution ? `<p><strong>Next Run:</strong> ${new Date(task.nextExecution).toLocaleString()}</p>` : ''}
                    ${task.lastExecuted ? `<p><strong>Last Run:</strong> ${new Date(task.lastExecuted).toLocaleString()}</p>` : ''}
                </div>
                <div class="task-actions">
                    ${task.status !== 'ACTIVE' ? `<button class="btn btn-success" onclick="dashboard.startTask(${task.id})">▶️ Start</button>` : ''}
                    ${task.status === 'ACTIVE' ? `<button class="btn btn-warning" onclick="dashboard.pauseTask(${task.id})">⏸️ Pause</button>` : ''}
                    ${task.status !== 'INACTIVE' ? `<button class="btn btn-secondary" onclick="dashboard.stopTask(${task.id})">⏹️ Stop</button>` : ''}
                    <button class="btn btn-danger" onclick="dashboard.deleteTask(${task.id})">🗑️ Delete</button>
                </div>
            </div>
        `).join('');
    }

    async startTask(taskId) {
        try {
            const response = await fetch(`${this.baseUrl}/scheduler/start/${taskId}`, {
                method: 'POST'
            });
            
            if (response.ok) {
                const result = await response.json();
                this.logActivity('Task Started', result.message, 'success');
                this.loadTasks();
                this.loadSystemStatus();
            } else {
                throw new Error('Failed to start task');
            }
        } catch (error) {
            this.logActivity('Error', `Failed to start task: ${error.message}`, 'error');
        }
    }

    async pauseTask(taskId) {
        try {
            const response = await fetch(`${this.baseUrl}/scheduler/pause/${taskId}`, {
                method: 'POST'
            });
            
            if (response.ok) {
                const result = await response.json();
                this.logActivity('Task Paused', result.message, 'warning');
                this.loadTasks();
                this.loadSystemStatus();
            } else {
                throw new Error('Failed to pause task');
            }
        } catch (error) {
            this.logActivity('Error', `Failed to pause task: ${error.message}`, 'error');
        }
    }

    async stopTask(taskId) {
        try {
            const response = await fetch(`${this.baseUrl}/scheduler/stop/${taskId}`, {
                method: 'POST'
            });
            
            if (response.ok) {
                const result = await response.json();
                this.logActivity('Task Stopped', result.message, 'info');
                this.loadTasks();
                this.loadSystemStatus();
            } else {
                throw new Error('Failed to stop task');
            }
        } catch (error) {
            this.logActivity('Error', `Failed to stop task: ${error.message}`, 'error');
        }
    }

    async deleteTask(taskId) {
        if (!confirm('Are you sure you want to delete this task?')) {
            return;
        }

        try {
            const response = await fetch(`${this.baseUrl}/tasks/${taskId}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                this.logActivity('Task Deleted', 'Task deleted successfully', 'info');
                this.loadTasks();
                this.loadSystemStatus();
            } else {
                throw new Error('Failed to delete task');
            }
        } catch (error) {
            this.logActivity('Error', `Failed to delete task: ${error.message}`, 'error');
        }
    }

    async loadSystemStatus() {
        try {
            const response = await fetch(`${this.baseUrl}/scheduler/status`);
            const status = await response.json();
            this.renderSystemStatus(status);
        } catch (error) {
            this.logActivity('Error', `Failed to load system status: ${error.message}`, 'error');
        }
    }

    renderSystemStatus(status) {
        const statusContainer = document.getElementById('systemStatus');
        
        statusContainer.innerHTML = `
            <div class="status-item">
                <div class="status-value">${status.activeScheduledTasks}</div>
                <div class="status-label">Active Scheduled</div>
            </div>
            <div class="status-item">
                <div class="status-value">${status.totalTasksInDB}</div>
                <div class="status-label">Total Tasks</div>
            </div>
            <div class="status-item">
                <div class="status-value">${status.activeTasks}</div>
                <div class="status-label">Active Tasks</div>
            </div>
            <div class="status-item">
                <div class="status-value">${status.tasksByStatus?.INACTIVE || 0}</div>
                <div class="status-label">Inactive Tasks</div>
            </div>
            <div class="status-item">
                <div class="status-value">${status.tasksByStatus?.PAUSED || 0}</div>
                <div class="status-label">Paused Tasks</div>
            </div>
            <div class="status-item">
                <div class="status-value">${status.tasksByStatus?.ERROR || 0}</div>
                <div class="status-label">Error Tasks</div>
            </div>
        `;

        // Update header counts
        document.getElementById('activeTasksCount').textContent = `Active: ${status.activeTasks}`;
        document.getElementById('totalTasksCount').textContent = `Total: ${status.totalTasksInDB}`;
    }

    async reloadScheduler() {
        try {
            const response = await fetch(`${this.baseUrl}/scheduler/reload`, {
                method: 'POST'
            });
            
            if (response.ok) {
                const result = await response.json();
                this.logActivity('Scheduler Reloaded', result.message, 'success');
                this.loadTasks();
                this.loadSystemStatus();
            } else {
                throw new Error('Failed to reload scheduler');
            }
        } catch (error) {
            this.logActivity('Error', `Failed to reload scheduler: ${error.message}`, 'error');
        }
    }

    updateTaskCount(count) {
        // This will be updated by loadSystemStatus
    }

    refreshAll() {
        this.logActivity('System', 'Refreshing all data...', 'info');
        this.loadTasks();
        this.loadSystemStatus();
    }

    logActivity(source, message, level = 'info') {
        const logContainer = document.getElementById('activityLog');
        const timestamp = new Date().toLocaleTimeString();
        
        const logEntry = document.createElement('div');
        logEntry.className = `log-entry ${level}`;
        logEntry.innerHTML = `<span class="timestamp">[${timestamp}]</span><strong>${source}:</strong> ${message}`;
        
        logContainer.insertBefore(logEntry, logContainer.firstChild);
        
        // Keep only last 50 log entries
        while (logContainer.children.length > 50) {
            logContainer.removeChild(logContainer.lastChild);
        }
    }

    clearLogs() {
        document.getElementById('activityLog').innerHTML = '';
        this.logActivity('System', 'Logs cleared', 'info');
    }

    startLiveUpdates() {
        // Refresh system status every 30 seconds
        setInterval(() => {
            this.loadSystemStatus();
        }, 30000);

        // Refresh tasks every 60 seconds
        setInterval(() => {
            this.loadTasks();
        }, 60000);
    }
}

// Initialize dashboard when page loads
let dashboard;
document.addEventListener('DOMContentLoaded', () => {
    dashboard = new TaskSchedulerDashboard();
});
