class SchedulerDashboard {
    constructor() {
        this.updateInterval = 2000; // 2 seconds
        this.taskLogMaxEntries = 50;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.startAutoRefresh();
        this.loadInitialData();
    }

    setupEventListeners() {
        document.getElementById('trigger-election').addEventListener('click', () => {
            this.triggerElection();
        });
    }

    startAutoRefresh() {
        setInterval(() => {
            this.updateDashboard();
        }, this.updateInterval);
    }

    async loadInitialData() {
        await this.updateDashboard();
    }

    async updateDashboard() {
        try {
            await Promise.all([
                this.updateLeadershipStatus(),
                this.updateClusterHealth(),
                this.updateSystemMetrics()
            ]);
        } catch (error) {
            console.error('Error updating dashboard:', error);
        }
    }

    async updateLeadershipStatus() {
        try {
            const response = await fetch('/api/scheduler/status');
            const data = await response.json();

            document.getElementById('current-leader').textContent = data.currentLeader || 'None';
            document.getElementById('leader-generation').textContent = data.leaderGeneration || '-';
            
            if (data.leaderExpiry) {
                const expiry = new Date(data.leaderExpiry);
                document.getElementById('lease-expiry').textContent = expiry.toLocaleTimeString();
            }

            // Add task activity if this node is leader
            if (data.isLeader) {
                this.addTaskLogEntry(`🔥 Processing tasks as leader (${data.nodeId})`);
            }
        } catch (error) {
            console.error('Error fetching leadership status:', error);
        }
    }

    async updateClusterHealth() {
        try {
            const response = await fetch('/api/scheduler/health');
            const nodes = await response.json();

            const healthyNodes = nodes.filter(node => 
                node.status === 'HEALTHY' || node.status === 'DEGRADED'
            );

            document.getElementById('healthy-count').textContent = healthyNodes.length;
            document.getElementById('total-count').textContent = nodes.length;

            this.renderNodesList(nodes);
        } catch (error) {
            console.error('Error fetching cluster health:', error);
        }
    }

    renderNodesList(nodes) {
        const container = document.getElementById('nodes-list');
        container.innerHTML = '';

        nodes.forEach(node => {
            const nodeDiv = document.createElement('div');
            nodeDiv.className = `node-item ${node.status.toLowerCase()}`;
            
            const statusIcon = this.getStatusIcon(node.status);
            const lastSeen = node.lastHeartbeat ? 
                new Date(node.lastHeartbeat).toLocaleTimeString() : 'Never';

            nodeDiv.innerHTML = `
                <div>
                    <strong>${node.nodeId}</strong>
                    <br>
                    <small>Last seen: ${lastSeen}</small>
                </div>
                <div style="text-align: right;">
                    <span style="font-size: 1.2em;">${statusIcon}</span>
                    <br>
                    <small>${node.status}</small>
                </div>
            `;
            
            container.appendChild(nodeDiv);
        });
    }

    getStatusIcon(status) {
        const icons = {
            'HEALTHY': '💚',
            'DEGRADED': '🟡', 
            'UNHEALTHY': '🔴',
            'UNKNOWN': '⚪'
        };
        return icons[status] || '❓';
    }

    async updateSystemMetrics() {
        // In a real implementation, this would fetch actual metrics
        // For demo purposes, we'll simulate some values
        const cpuUsage = Math.random() * 60 + 10; // 10-70%
        const memoryUsage = Math.random() * 50 + 20; // 20-70%

        document.getElementById('cpu-bar').style.width = `${cpuUsage}%`;
        document.getElementById('cpu-value').textContent = `${cpuUsage.toFixed(1)}%`;
        
        document.getElementById('memory-bar').style.width = `${memoryUsage}%`;
        document.getElementById('memory-value').textContent = `${memoryUsage.toFixed(1)}%`;

        // Change colors based on usage
        const cpuBar = document.getElementById('cpu-bar');
        const memoryBar = document.getElementById('memory-bar');
        
        cpuBar.style.background = cpuUsage > 70 ? 
            'linear-gradient(90deg, #f56565, #e53e3e)' : 
            'linear-gradient(90deg, #48bb78, #38a169)';
            
        memoryBar.style.background = memoryUsage > 70 ? 
            'linear-gradient(90deg, #f56565, #e53e3e)' : 
            'linear-gradient(90deg, #48bb78, #38a169)';
    }

    async triggerElection() {
        try {
            const button = document.getElementById('trigger-election');
            button.disabled = true;
            button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Triggering...';

            const response = await fetch('/api/scheduler/election/trigger', {
                method: 'POST'
            });

            if (response.ok) {
                const result = await response.json();
                this.addTaskLogEntry(`⚡ Manual election triggered: ${result.message}`);
                
                // Immediately refresh status
                setTimeout(() => this.updateLeadershipStatus(), 1000);
            } else {
                throw new Error('Failed to trigger election');
            }
        } catch (error) {
            console.error('Error triggering election:', error);
            this.addTaskLogEntry(`❌ Failed to trigger election: ${error.message}`);
        } finally {
            const button = document.getElementById('trigger-election');
            button.disabled = false;
            button.innerHTML = '<i class="fas fa-bolt"></i> Trigger Election';
        }
    }

    addTaskLogEntry(message) {
        const logContainer = document.getElementById('task-log');
        const timestamp = new Date().toLocaleTimeString();
        
        const logEntry = document.createElement('p');
        logEntry.className = 'log-item';
        logEntry.textContent = `[${timestamp}] ${message}`;
        
        logContainer.appendChild(logEntry);
        
        // Keep only recent entries
        while (logContainer.children.length > this.taskLogMaxEntries) {
            logContainer.removeChild(logContainer.firstChild);
        }
        
        // Auto scroll to bottom
        logContainer.scrollTop = logContainer.scrollHeight;
    }
}

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new SchedulerDashboard();
});
