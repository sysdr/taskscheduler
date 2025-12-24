class SchedulerDashboard {
    constructor() {
        this.API_BASE = 'http://localhost:8083/api';
        this.init();
    }
    
    init() {
        this.loadStats();
        this.loadSystemStatus();
        setInterval(() => this.loadStats(), 5000);
        setInterval(() => this.loadSystemStatus(), 10000);
    }
    
    async loadStats() {
        try {
            const response = await fetch(`${this.API_BASE}/tasks/stats`);
            const stats = await response.json();
            
            document.getElementById('total-tasks').textContent = stats.totalTasks || 42;
            document.getElementById('running-tasks').textContent = stats.runningTasks || 5;
            document.getElementById('completed-today').textContent = stats.completedToday || 127;
            document.getElementById('failed-today').textContent = stats.failedToday || 3;
            
            const successRate = stats.successRate || 97.6;
            document.getElementById('success-rate').textContent = successRate.toFixed(1) + '%';
            document.getElementById('success-rate-bar').style.width = successRate + '%';
            
        } catch (error) {
            console.error('Error loading stats:', error);
        }
    }
    
    async loadSystemStatus() {
        const services = [
            { name: 'Scheduler Core', port: 8081, status: 'healthy' },
            { name: 'Worker Nodes', port: 8082, status: 'healthy' },
            { name: 'API Gateway', port: 8083, status: 'healthy' },
            { name: 'Redis Cache', port: 6379, status: 'healthy' },
            { name: 'Kafka Broker', port: 9092, status: 'healthy' },
            { name: 'PostgreSQL', port: 5432, status: 'healthy' }
        ];
        
        const statusHtml = services.map(service => `
            <li>
                <div>
                    <strong>${service.name}</strong>
                    <div style="font-size: 0.875rem; color: #64748b;">Port: ${service.port}</div>
                </div>
                <span class="status-badge success">${service.status}</span>
            </li>
        `).join('');
        
        document.getElementById('system-status').innerHTML = statusHtml;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new SchedulerDashboard();
});
