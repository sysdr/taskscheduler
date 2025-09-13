package com.scheduler.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String dashboard() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Task Scheduler - Leader Failover Dashboard</title>
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #333; min-height: 100vh; }
                    .container { max-width: 1400px; margin: 0 auto; padding: 20px; }
                    .header { background: rgba(255, 255, 255, 0.95); padding: 20px 30px; border-radius: 15px; margin-bottom: 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); backdrop-filter: blur(10px); }
                    .header h1 { color: #4a5568; font-size: 2rem; display: flex; align-items: center; gap: 15px; }
                    .header h1 i { color: #f6ad55; }
                    .node-info { display: flex; flex-direction: column; align-items: flex-end; gap: 8px; }
                    .node-id { font-size: 0.9rem; color: #666; font-weight: 500; }
                    .leader-badge { padding: 8px 16px; border-radius: 20px; font-weight: bold; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 1px; }
                    .leader-badge.leader { background: linear-gradient(45deg, #48bb78, #38a169); color: white; }
                    .leader-badge.follower { background: linear-gradient(45deg, #ed8936, #dd6b20); color: white; }
                    .dashboard-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 25px; }
                    .card { background: rgba(255, 255, 255, 0.95); padding: 25px; border-radius: 15px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.2); }
                    .card h2 { color: #4a5568; margin-bottom: 20px; display: flex; align-items: center; gap: 12px; font-size: 1.3rem; }
                    .status-item { display: flex; justify-content: space-between; margin-bottom: 12px; padding: 8px 0; border-bottom: 1px solid #e2e8f0; }
                    .status-item label { font-weight: 600; color: #4a5568; }
                    .status-item span { color: #2d3748; }
                    .btn { background: linear-gradient(45deg, #4299e1, #3182ce); color: white; border: none; padding: 12px 24px; border-radius: 8px; cursor: pointer; font-weight: 600; transition: all 0.3s ease; display: flex; align-items: center; gap: 8px; margin-top: 15px; }
                    .btn:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(66, 153, 225, 0.4); }
                    .btn-warning { background: linear-gradient(45deg, #ed8936, #dd6b20); }
                    .btn-warning:hover { box-shadow: 0 5px 15px rgba(237, 137, 54, 0.4); }
                    .health-summary { display: flex; gap: 30px; margin-bottom: 20px; }
                    .health-stat { text-align: center; }
                    .health-count { display: block; font-size: 2.5rem; font-weight: bold; color: #48bb78; margin-bottom: 5px; }
                    .health-stat label { color: #666; font-size: 0.9rem; }
                    .node-item { display: flex; justify-content: space-between; align-items: center; padding: 10px; margin: 8px 0; border-radius: 8px; border-left: 4px solid #48bb78; background: rgba(72, 187, 120, 0.1); }
                    .node-item.degraded { border-left-color: #ed8936; background: rgba(237, 137, 54, 0.1); }
                    .node-item.unhealthy { border-left-color: #f56565; background: rgba(245, 101, 101, 0.1); }
                    #task-log { max-height: 200px; overflow-y: auto; background: rgba(247, 250, 252, 0.8); padding: 15px; border-radius: 8px; font-family: 'Courier New', monospace; font-size: 0.9rem; }
                    .log-item { margin: 5px 0; padding: 2px 0; color: #4a5568; }
                    .metrics-grid { display: flex; flex-direction: column; gap: 20px; }
                    .metric { display: flex; align-items: center; gap: 15px; }
                    .metric label { min-width: 100px; font-weight: 600; color: #4a5568; }
                    .metric-bar { flex: 1; height: 8px; background: rgba(226, 232, 240, 0.8); border-radius: 4px; overflow: hidden; }
                    .metric-fill { height: 100%; background: linear-gradient(90deg, #48bb78, #38a169); transition: width 0.3s ease; border-radius: 4px; }
                    .metric span { min-width: 50px; text-align: right; font-weight: 600; color: #2d3748; }
                </style>
            </head>
            <body>
                <div class="container">
                    <header class="header">
                        <h1><i class="fas fa-crown"></i> Task Scheduler - Leader Failover</h1>
                        <div class="node-info">
                            <span class="node-id" id="node-id">Node: Loading...</span>
                            <span class="leader-badge" id="leader-badge">STATUS</span>
                        </div>
                    </header>

                    <div class="dashboard-grid">
                        <div class="card leadership-status">
                            <h2><i class="fas fa-vote-yea"></i> Leadership Status</h2>
                            <div id="leadership-info">
                                <div class="status-item">
                                    <label>Current Leader:</label>
                                    <span id="current-leader">Loading...</span>
                                </div>
                                <div class="status-item">
                                    <label>Generation:</label>
                                    <span id="leader-generation">-</span>
                                </div>
                                <div class="status-item">
                                    <label>Lease Expires:</label>
                                    <span id="lease-expiry">-</span>
                                </div>
                            </div>
                            <button id="trigger-election" class="btn btn-warning">
                                <i class="fas fa-bolt"></i> Trigger Election
                            </button>
                        </div>

                        <div class="card node-health">
                            <h2><i class="fas fa-heartbeat"></i> Cluster Health</h2>
                            <div id="health-summary">
                                <div class="health-stat">
                                    <span class="health-count" id="healthy-count">0</span>
                                    <label>Healthy Nodes</label>
                                </div>
                                <div class="health-stat">
                                    <span class="health-count" id="total-count">0</span>
                                    <label>Total Nodes</label>
                                </div>
                            </div>
                            <div id="nodes-list"></div>
                        </div>

                        <div class="card task-activity">
                            <h2><i class="fas fa-tasks"></i> Task Activity</h2>
                            <div id="task-log">
                                <p class="log-item">System initialized - monitoring for task activity...</p>
                            </div>
                        </div>

                        <div class="card system-metrics">
                            <h2><i class="fas fa-chart-line"></i> System Metrics</h2>
                            <div class="metrics-grid">
                                <div class="metric">
                                    <label>CPU Usage</label>
                                    <div class="metric-bar">
                                        <div class="metric-fill" id="cpu-bar" style="width: 0%"></div>
                                    </div>
                                    <span id="cpu-value">0%</span>
                                </div>
                                <div class="metric">
                                    <label>Memory Usage</label>
                                    <div class="metric-bar">
                                        <div class="metric-fill" id="memory-bar" style="width: 0%"></div>
                                    </div>
                                    <span id="memory-value">0%</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <script>
                    class SchedulerDashboard {
                        constructor() {
                            this.updateInterval = 2000;
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
                                document.getElementById('node-id').textContent = 'Node: ' + data.nodeId;
                                
                                const leaderBadge = document.getElementById('leader-badge');
                                if (data.isLeader) {
                                    leaderBadge.textContent = 'LEADER';
                                    leaderBadge.className = 'leader-badge leader';
                                    this.addTaskLogEntry(`🔥 Processing tasks as leader (${data.nodeId})`);
                                } else {
                                    leaderBadge.textContent = 'FOLLOWER';
                                    leaderBadge.className = 'leader-badge follower';
                                }
                                
                                if (data.leaderExpiry) {
                                    const expiry = new Date(data.leaderExpiry);
                                    document.getElementById('lease-expiry').textContent = expiry.toLocaleTimeString();
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
                            try {
                                const response = await fetch('/api/scheduler/health');
                                const nodes = await response.json();
                                
                                if (nodes.length > 0) {
                                    const currentNode = nodes[0];
                                    const cpuUsage = currentNode.cpuUsage || 0;
                                    const memoryUsage = currentNode.memoryUsage || 0;

                                    document.getElementById('cpu-bar').style.width = `${cpuUsage}%`;
                                    document.getElementById('cpu-value').textContent = `${cpuUsage.toFixed(1)}%`;
                                    
                                    document.getElementById('memory-bar').style.width = `${memoryUsage}%`;
                                    document.getElementById('memory-value').textContent = `${memoryUsage.toFixed(1)}%`;

                                    const cpuBar = document.getElementById('cpu-bar');
                                    const memoryBar = document.getElementById('memory-bar');
                                    
                                    cpuBar.style.background = cpuUsage > 70 ? 
                                        'linear-gradient(90deg, #f56565, #e53e3e)' : 
                                        'linear-gradient(90deg, #48bb78, #38a169)';
                                        
                                    memoryBar.style.background = memoryUsage > 70 ? 
                                        'linear-gradient(90deg, #f56565, #e53e3e)' : 
                                        'linear-gradient(90deg, #48bb78, #38a169)';
                                }
                            } catch (error) {
                                console.error('Error fetching system metrics:', error);
                            }
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
                            
                            while (logContainer.children.length > this.taskLogMaxEntries) {
                                logContainer.removeChild(logContainer.firstChild);
                            }
                            
                            logContainer.scrollTop = logContainer.scrollHeight;
                        }
                    }

                    document.addEventListener('DOMContentLoaded', () => {
                        new SchedulerDashboard();
                    });
                </script>
            </body>
            </html>
            """;
    }
}
