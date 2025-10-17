class TaskDashboard {
    constructor() {
        this.tasks = [];
        this.isLoading = false;
        this.init();
    }

    init() {
        this.attachEventListeners();
        this.loadTasks();
        this.startAutoRefresh();
    }

    attachEventListeners() {
        document.getElementById('createEmailTask').addEventListener('click', () => this.createSampleTask('email'));
        document.getElementById('createReportTask').addEventListener('click', () => this.createSampleTask('report'));
        document.getElementById('createDataTask').addEventListener('click', () => this.createSampleTask('data'));
        document.getElementById('refreshTasks').addEventListener('click', () => this.loadTasks());
    }

    async createSampleTask(type) {
        try {
            const response = await fetch(`/api/tasks/sample/${type}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
            
            if (response.ok) {
                const task = await response.json();
                console.log(`Created ${type} task:`, task);
                this.loadTasks(); // Refresh the task list
            }
        } catch (error) {
            console.error('Error creating task:', error);
        }
    }

    async loadTasks() {
        if (this.isLoading) return;
        
        this.isLoading = true;
        this.showLoading();

        try {
            const response = await fetch('/api/tasks');
            if (response.ok) {
                this.tasks = await response.json();
                this.renderTasks();
                this.updateStats();
            }
        } catch (error) {
            console.error('Error loading tasks:', error);
        } finally {
            this.isLoading = false;
        }
    }

    renderTasks() {
        const container = document.getElementById('tasksList');
        if (this.tasks.length === 0) {
            container.innerHTML = '<div class="loading">No tasks found. Create some tasks to get started!</div>';
            return;
        }

        const html = this.tasks.slice(0, 20).map(task => this.renderTask(task)).join('');
        container.innerHTML = html;
    }

    renderTask(task) {
        const statusClass = task.status.toLowerCase();
        const timeAgo = this.getTimeAgo(task.createdAt);
        
        return `
            <div class="task-item ${statusClass}">
                <div class="task-header">
                    <div class="task-name">${task.name}</div>
                    <div class="task-status status-${statusClass}">${task.status}</div>
                </div>
                <div class="task-meta">
                    <strong>Type:</strong> ${task.type} | 
                    <strong>Created:</strong> ${timeAgo}
                    ${task.result ? `<br><strong>Result:</strong> ${task.result}` : ''}
                    ${task.errorMessage ? `<br><strong>Error:</strong> ${task.errorMessage}` : ''}
                </div>
            </div>
        `;
    }

    updateStats() {
        const stats = this.calculateStats();
        
        document.getElementById('totalTasks').textContent = stats.total;
        document.getElementById('pendingTasks').textContent = stats.pending;
        document.getElementById('executingTasks').textContent = stats.executing;
        document.getElementById('completedTasks').textContent = stats.completed;
        document.getElementById('failedTasks').textContent = stats.failed;
    }

    calculateStats() {
        return {
            total: this.tasks.length,
            pending: this.tasks.filter(t => t.status === 'PENDING' || t.status === 'SUBMITTED').length,
            executing: this.tasks.filter(t => t.status === 'EXECUTING').length,
            completed: this.tasks.filter(t => t.status === 'COMPLETED').length,
            failed: this.tasks.filter(t => t.status === 'FAILED').length
        };
    }

    showLoading() {
        document.getElementById('tasksList').innerHTML = `
            <div class="loading">
                <div class="spinner"></div>
                Loading tasks...
            </div>
        `;
    }

    getTimeAgo(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now - date;
        const diffSecs = Math.floor(diffMs / 1000);
        const diffMins = Math.floor(diffSecs / 60);
        const diffHours = Math.floor(diffMins / 60);

        if (diffSecs < 60) return `${diffSecs}s ago`;
        if (diffMins < 60) return `${diffMins}m ago`;
        if (diffHours < 24) return `${diffHours}h ago`;
        return date.toLocaleDateString();
    }

    startAutoRefresh() {
        setInterval(() => {
            if (!this.isLoading) {
                this.loadTasks();
            }
        }, 5000); // Refresh every 5 seconds
    }
}

// Initialize dashboard when page loads
document.addEventListener('DOMContentLoaded', () => {
    new TaskDashboard();
});
