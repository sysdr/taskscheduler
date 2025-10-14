class CircuitBreakerDashboard {
    constructor() {
        this.init();
        this.connectWebSocket();
        this.startPolling();
    }

    init() {
        this.attachEventListeners();
        this.loadInitialData();
    }

    attachEventListeners() {
        document.getElementById('taskForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.createTask();
        });

        document.getElementById('refreshBtn').addEventListener('click', () => {
            this.refreshData();
        });
    }

    async createTask() {
        const name = document.getElementById('taskName').value;
        const type = document.getElementById('taskType').value;

        try {
            const response = await fetch('/api/tasks', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ name, type })
            });

            if (response.ok) {
                document.getElementById('taskName').value = '';
                this.showNotification('Task created successfully!', 'success');
                setTimeout(() => this.refreshTasks(), 1000);
            } else {
                this.showNotification('Failed to create task', 'error');
            }
        } catch (error) {
            this.showNotification('Error creating task: ' + error.message, 'error');
        }
    }

    async refreshTasks() {
        try {
            const response = await fetch('/api/tasks/recent');
            const tasks = await response.json();
            this.updateTasksTable(tasks);
        } catch (error) {
            console.error('Error loading tasks:', error);
        }
    }

    updateTasksTable(tasks) {
        const tbody = document.querySelector('#tasksTable tbody');
        tbody.innerHTML = '';

        tasks.forEach(task => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${task.id}</td>
                <td>${task.name}</td>
                <td>${task.type}</td>
                <td><span class="status status-${task.status.toLowerCase()}">${task.status}</span></td>
                <td>${this.formatTime(task.createdAt)}</td>
                <td>${task.errorMessage || ''}</td>
            `;
            tbody.appendChild(row);
        });
    }

    async loadCircuitBreakerMetrics() {
        try {
            const response = await fetch('/actuator/circuitbreakers');
            const data = await response.json();
            
            // Update circuit breaker states
            Object.keys(data.circuitBreakers).forEach(name => {
                const cb = data.circuitBreakers[name];
                this.updateCircuitBreakerUI(name, cb);
            });
        } catch (error) {
            console.error('Error loading circuit breaker metrics:', error);
        }
    }

    updateCircuitBreakerUI(name, circuitBreaker) {
        const stateElement = document.getElementById(`${name}CircuitState`);
        const failureRateElement = document.getElementById(`${name}FailureRate`);
        
        if (stateElement) {
            stateElement.textContent = circuitBreaker.state;
            stateElement.className = `circuit-state ${circuitBreaker.state.toLowerCase()}`;
        }
        
        if (failureRateElement) {
            const failureRate = circuitBreaker.failureRate || 0;
            failureRateElement.textContent = `${Math.round(failureRate)}%`;
        }
    }

    connectWebSocket() {
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            
            this.stompClient.subscribe('/topic/circuit-breaker-events', (message) => {
                const event = JSON.parse(message.body);
                this.handleCircuitBreakerEvent(event);
            });
        });
    }

    handleCircuitBreakerEvent(event) {
        this.showNotification(
            `${event.circuitBreakerName} circuit breaker ${event.eventType}`, 
            event.eventType === 'OPENED' ? 'warning' : 'info'
        );
        
        setTimeout(() => this.loadCircuitBreakerMetrics(), 1000);
    }

    startPolling() {
        // Refresh data every 5 seconds
        setInterval(() => {
            this.refreshData();
        }, 5000);
    }

    async refreshData() {
        await Promise.all([
            this.refreshTasks(),
            this.loadCircuitBreakerMetrics()
        ]);
    }

    loadInitialData() {
        this.refreshData();
    }

    formatTime(dateTime) {
        const date = new Date(dateTime);
        return date.toLocaleTimeString();
    }

    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
            ${message}
        `;
        
        // Add to DOM
        document.body.appendChild(notification);
        
        // Remove after 3 seconds
        setTimeout(() => {
            notification.remove();
        }, 3000);
    }
}

// Service control functions
async function toggleService(serviceName) {
    // Get the button element to determine current state
    const button = event.target;
    const buttonText = button.textContent.trim();
    
    // Determine if we're enabling or disabling based on button text
    let enable = false;
    if (serviceName === 'payment') {
        enable = buttonText.includes('Simulate Failure');
    } else if (serviceName === 'notification') {
        enable = buttonText.includes('Make Flakey');
    }
    
    try {
        const response = await fetch(`/api/tasks/simulate-failure/${serviceName}?enable=${enable}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            const message = await response.text();
            dashboard.showNotification(message, 'warning');
            setTimeout(() => location.reload(), 2000);
        }
    } catch (error) {
        dashboard.showNotification('Error toggling service: ' + error.message, 'error');
    }
}

// Initialize dashboard when page loads
let dashboard;
document.addEventListener('DOMContentLoaded', () => {
    dashboard = new CircuitBreakerDashboard();
});
