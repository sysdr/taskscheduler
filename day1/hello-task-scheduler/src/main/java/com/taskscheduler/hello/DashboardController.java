package com.taskscheduler.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard Controller - Provides REST endpoints for monitoring task scheduler
 */
@RestController
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private HelloSchedulerService schedulerService;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private long startTime = System.currentTimeMillis();
    
    /**
     * Get current status of the task scheduler
     */
    @GetMapping("/api/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - startTime) / 1000;
        
        status.put("application", "Hello Task Scheduler");
        status.put("version", "1.0.0");
        status.put("status", elapsedSeconds >= 60 ? "SHUTDOWN" : "RUNNING");
        status.put("startTime", LocalDateTime.now().minusSeconds(elapsedSeconds).format(formatter));
        status.put("currentTime", LocalDateTime.now().format(formatter));
        status.put("elapsedSeconds", elapsedSeconds);
        status.put("executionCount", schedulerService.getExecutionCount());
        status.put("expectedExecutions", 6);
        status.put("executionInterval", "10 seconds");
        status.put("maxRuntime", "60 seconds");
        status.put("remainingTime", Math.max(0, 60 - elapsedSeconds));
        
        return status;
    }
    
    /**
     * Get a Google Cloud Skills Boost inspired HTML dashboard
     */
    @GetMapping("/dashboard")
    public String getDashboard() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Task Scheduler Dashboard - Google Cloud Style</title>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link href="https://fonts.googleapis.com/css2?family=Google+Sans:wght@400;500;700&display=swap" rel="stylesheet">
                <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    
                    body { 
                        font-family: 'Google Sans', -apple-system, BlinkMacSystemFont, sans-serif; 
                        background: #f8f9fa; 
                        color: #202124; 
                        line-height: 1.6;
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #4285f4 0%, #34a853 50%, #fbbc04 100%);
                        color: white;
                        padding: 20px 0;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    
                    .header-content {
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 0 20px;
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                    }
                    
                    .logo {
                        display: flex;
                        align-items: center;
                        gap: 12px;
                        font-size: 24px;
                        font-weight: 500;
                    }
                    
                    .logo-icon {
                        font-size: 32px;
                        color: #fbbc04;
                    }
                    
                    .header-actions {
                        display: flex;
                        gap: 12px;
                        align-items: center;
                    }
                    
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    
                    .dashboard-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
                        gap: 24px;
                        margin-top: 24px;
                    }
                    
                    .card {
                        background: white;
                        border-radius: 12px;
                        padding: 24px;
                        box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24);
                        transition: all 0.3s cubic-bezier(.25,.8,.25,1);
                        border: 1px solid #e8eaed;
                    }
                    
                    .card:hover {
                        box-shadow: 0 14px 28px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22);
                        transform: translateY(-2px);
                    }
                    
                    .card-header {
                        display: flex;
                        align-items: center;
                        gap: 12px;
                        margin-bottom: 20px;
                        padding-bottom: 16px;
                        border-bottom: 1px solid #e8eaed;
                    }
                    
                    .card-icon {
                        width: 40px;
                        height: 40px;
                        border-radius: 8px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 20px;
                        color: white;
                    }
                    
                    .card-title {
                        font-size: 18px;
                        font-weight: 500;
                        color: #202124;
                    }
                    
                    .metric {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        padding: 12px 0;
                        border-bottom: 1px solid #f1f3f4;
                    }
                    
                    .metric:last-child {
                        border-bottom: none;
                    }
                    
                    .metric-label {
                        font-weight: 500;
                        color: #5f6368;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                    }
                    
                    .metric-value {
                        font-family: 'Google Sans Mono', 'Roboto Mono', monospace;
                        font-weight: 500;
                        font-size: 14px;
                    }
                    
                    .status-running {
                        color: #34a853;
                        background: #e6f4ea;
                        padding: 4px 12px;
                        border-radius: 16px;
                        font-size: 12px;
                        font-weight: 500;
                    }
                    
                    .status-shutdown {
                        color: #ea4335;
                        background: #fce8e6;
                        padding: 4px 12px;
                        border-radius: 16px;
                        font-size: 12px;
                        font-weight: 500;
                    }
                    
                    .progress-container {
                        margin: 16px 0;
                    }
                    
                    .progress-bar {
                        width: 100%;
                        height: 8px;
                        background: #e8eaed;
                        border-radius: 4px;
                        overflow: hidden;
                        margin: 8px 0;
                    }
                    
                    .progress-fill {
                        height: 100%;
                        background: linear-gradient(90deg, #4285f4, #34a853);
                        transition: width 0.3s ease;
                        border-radius: 4px;
                    }
                    
                    .progress-text {
                        font-size: 12px;
                        color: #5f6368;
                        text-align: center;
                    }
                    
                    .btn {
                        background: #4285f4;
                        color: white;
                        border: none;
                        padding: 12px 24px;
                        border-radius: 8px;
                        font-family: 'Google Sans', sans-serif;
                        font-weight: 500;
                        cursor: pointer;
                        transition: all 0.2s ease;
                        display: inline-flex;
                        align-items: center;
                        gap: 8px;
                    }
                    
                    .btn:hover {
                        background: #3367d6;
                        transform: translateY(-1px);
                        box-shadow: 0 4px 8px rgba(66, 133, 244, 0.3);
                    }
                    
                    .btn-secondary {
                        background: #f1f3f4;
                        color: #5f6368;
                    }
                    
                    .btn-secondary:hover {
                        background: #e8eaed;
                        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                    }
                    
                    .controls {
                        display: flex;
                        gap: 16px;
                        align-items: center;
                        margin: 20px 0;
                        flex-wrap: wrap;
                    }
                    
                    .checkbox-container {
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        font-size: 14px;
                        color: #5f6368;
                    }
                    
                    .checkbox-container input[type="checkbox"] {
                        width: 16px;
                        height: 16px;
                        accent-color: #4285f4;
                    }
                    
                    .log-section {
                        max-height: 300px;
                        overflow-y: auto;
                        background: #f8f9fa;
                        border-radius: 8px;
                        padding: 16px;
                        border: 1px solid #e8eaed;
                    }
                    
                    .log-entry {
                        font-family: 'Google Sans Mono', 'Roboto Mono', monospace;
                        font-size: 13px;
                        padding: 8px 12px;
                        margin: 4px 0;
                        border-radius: 6px;
                        background: white;
                        border-left: 4px solid #4285f4;
                        box-shadow: 0 1px 2px rgba(0,0,0,0.1);
                    }
                    
                    .log-entry.success {
                        border-left-color: #34a853;
                        background: #f0f9ff;
                    }
                    
                    .log-entry.warning {
                        border-left-color: #fbbc04;
                        background: #fffbf0;
                    }
                    
                    .log-entry.error {
                        border-left-color: #ea4335;
                        background: #fef0f0;
                    }
                    
                    .stats-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
                        gap: 16px;
                        margin: 16px 0;
                    }
                    
                    .stat-item {
                        text-align: center;
                        padding: 16px;
                        background: #f8f9fa;
                        border-radius: 8px;
                        border: 1px solid #e8eaed;
                    }
                    
                    .stat-number {
                        font-size: 24px;
                        font-weight: 700;
                        color: #4285f4;
                        margin-bottom: 4px;
                    }
                    
                    .stat-label {
                        font-size: 12px;
                        color: #5f6368;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }
                    
                    @media (max-width: 768px) {
                        .dashboard-grid {
                            grid-template-columns: 1fr;
                        }
                        
                        .header-content {
                            flex-direction: column;
                            gap: 16px;
                        }
                        
                        .controls {
                            flex-direction: column;
                            align-items: stretch;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="header-content">
                        <div class="logo">
                            <span class="material-icons logo-icon">schedule</span>
                            <span>Task Scheduler Dashboard</span>
                        </div>
                        <div class="header-actions">
                            <button class="btn btn-secondary" onclick="refreshStatus()">
                                <span class="material-icons">refresh</span>
                                Refresh
                            </button>
                        </div>
                    </div>
                </div>
                
                <div class="container">
                    <div class="dashboard-grid">
                        <!-- Application Status Card -->
                        <div class="card">
                            <div class="card-header">
                                <div class="card-icon" style="background: #4285f4;">
                                    <span class="material-icons">monitoring</span>
                                </div>
                                <div class="card-title">Application Status</div>
                            </div>
                            <div id="status-content">
                                <div class="metric">
                                    <span class="metric-label">
                                        <span class="material-icons" style="font-size: 16px;">circle</span>
                                        Status
                                    </span>
                                    <span class="metric-value" id="status">Loading...</span>
                                </div>
                                <div class="metric">
                                    <span class="metric-label">
                                        <span class="material-icons" style="font-size: 16px;">play_circle</span>
                                        Executions
                                    </span>
                                    <span class="metric-value" id="executions">-</span>
                                </div>
                                <div class="metric">
                                    <span class="metric-label">
                                        <span class="material-icons" style="font-size: 16px;">timer</span>
                                        Elapsed Time
                                    </span>
                                    <span class="metric-value" id="elapsed">-</span>
                                </div>
                                <div class="metric">
                                    <span class="metric-label">
                                        <span class="material-icons" style="font-size: 16px;">hourglass_empty</span>
                                        Remaining Time
                                    </span>
                                    <span class="metric-value" id="remaining">-</span>
                                </div>
                                <div class="progress-container">
                                    <div class="progress-bar">
                                        <div class="progress-fill" id="progress" style="width: 0%"></div>
                                    </div>
                                    <div class="progress-text" id="progress-text">0% Complete</div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Configuration Card -->
                        <div class="card">
                            <div class="card-header">
                                <div class="card-icon" style="background: #34a853;">
                                    <span class="material-icons">settings</span>
                                </div>
                                <div class="card-title">Configuration</div>
                            </div>
                            <div class="metric">
                                <span class="metric-label">
                                    <span class="material-icons" style="font-size: 16px;">schedule</span>
                                    Execution Interval
                                </span>
                                <span class="metric-value">10 seconds</span>
                            </div>
                            <div class="metric">
                                <span class="metric-label">
                                    <span class="material-icons" style="font-size: 16px;">timer_off</span>
                                    Max Runtime
                                </span>
                                <span class="metric-value">60 seconds</span>
                            </div>
                            <div class="metric">
                                <span class="metric-label">
                                    <span class="material-icons" style="font-size: 16px;">check_circle</span>
                                    Expected Executions
                                </span>
                                <span class="metric-value">6</span>
                            </div>
                        </div>
                        
                        <!-- Statistics Card -->
                        <div class="card">
                            <div class="card-header">
                                <div class="card-icon" style="background: #fbbc04;">
                                    <span class="material-icons">analytics</span>
                                </div>
                                <div class="card-title">Statistics</div>
                            </div>
                            <div class="stats-grid">
                                <div class="stat-item">
                                    <div class="stat-number" id="stat-executions">0</div>
                                    <div class="stat-label">Executions</div>
                                </div>
                                <div class="stat-item">
                                    <div class="stat-number" id="stat-success-rate">100%</div>
                                    <div class="stat-label">Success Rate</div>
                                </div>
                                <div class="stat-item">
                                    <div class="stat-number" id="stat-uptime">0s</div>
                                    <div class="stat-label">Uptime</div>
                                </div>
                                <div class="stat-item">
                                    <div class="stat-number" id="stat-remaining">60s</div>
                                    <div class="stat-label">Remaining</div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Recent Activity Card -->
                        <div class="card" style="grid-column: 1 / -1;">
                            <div class="card-header">
                                <div class="card-icon" style="background: #ea4335;">
                                    <span class="material-icons">history</span>
                                </div>
                                <div class="card-title">Recent Activity</div>
                            </div>
                            <div class="controls">
                                <label class="checkbox-container">
                                    <input type="checkbox" id="autoRefresh" checked>
                                    Auto-refresh every 2 seconds
                                </label>
                            </div>
                            <div class="log-section" id="log-section">
                                <div class="log-entry success">
                                    <span class="material-icons" style="font-size: 16px; vertical-align: middle;">rocket_launch</span>
                                    Application started successfully
                                </div>
                                <div class="log-entry success">
                                    <span class="material-icons" style="font-size: 16px; vertical-align: middle;">check_circle</span>
                                    Scheduler service initialized
                                </div>
                                <div class="log-entry">
                                    <span class="material-icons" style="font-size: 16px; vertical-align: middle;">info</span>
                                    Will run for 60 seconds, executing every 10 seconds
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <script>
                    let autoRefreshInterval;
                    let lastExecutionCount = 0;
                    
                    function refreshStatus() {
                        fetch('/api/status')
                            .then(response => response.json())
                            .then(data => {
                                // Update status
                                const statusElement = document.getElementById('status');
                                statusElement.textContent = data.status;
                                statusElement.className = 'metric-value ' + (data.status === 'RUNNING' ? 'status-running' : 'status-shutdown');
                                
                                // Update metrics
                                document.getElementById('executions').textContent = data.executionCount + ' / ' + data.expectedExecutions;
                                document.getElementById('elapsed').textContent = data.elapsedSeconds + 's';
                                document.getElementById('remaining').textContent = data.remainingTime + 's';
                                
                                // Update progress
                                const progressPercent = Math.min(100, (data.elapsedSeconds / 60) * 100);
                                document.getElementById('progress').style.width = progressPercent + '%';
                                document.getElementById('progress-text').textContent = Math.round(progressPercent) + '% Complete';
                                
                                // Update statistics
                                document.getElementById('stat-executions').textContent = data.executionCount;
                                document.getElementById('stat-uptime').textContent = data.elapsedSeconds + 's';
                                document.getElementById('stat-remaining').textContent = data.remainingTime + 's';
                                document.getElementById('stat-success-rate').textContent = data.executionCount > 0 ? '100%' : '0%';
                                
                                // Add log entry for new executions
                                if (data.executionCount > lastExecutionCount) {
                                    const logSection = document.getElementById('log-section');
                                    const timestamp = new Date().toLocaleTimeString();
                                    const logEntry = document.createElement('div');
                                    logEntry.className = 'log-entry success';
                                    logEntry.innerHTML = `
                                        <span class="material-icons" style="font-size: 16px; vertical-align: middle;">play_circle</span>
                                        Execution #${data.executionCount} completed at ${timestamp}
                                    `;
                                    logSection.appendChild(logEntry);
                                    logSection.scrollTop = logSection.scrollHeight;
                                    lastExecutionCount = data.executionCount;
                                }
                                
                                // Add shutdown log
                                if (data.status === 'SHUTDOWN' && data.elapsedSeconds >= 60) {
                                    const logSection = document.getElementById('log-section');
                                    const logEntry = document.createElement('div');
                                    logEntry.className = 'log-entry warning';
                                    logEntry.innerHTML = `
                                        <span class="material-icons" style="font-size: 16px; vertical-align: middle;">stop_circle</span>
                                        Application shutdown after 60 seconds - ${data.executionCount} executions completed
                                    `;
                                    logSection.appendChild(logEntry);
                                    logSection.scrollTop = logSection.scrollHeight;
                                }
                            })
                            .catch(error => {
                                console.error('Error fetching status:', error);
                                const statusElement = document.getElementById('status');
                                statusElement.textContent = 'ERROR';
                                statusElement.className = 'metric-value status-shutdown';
                                
                                const logSection = document.getElementById('log-section');
                                const logEntry = document.createElement('div');
                                logEntry.className = 'log-entry error';
                                logEntry.innerHTML = `
                                    <span class="material-icons" style="font-size: 16px; vertical-align: middle;">error</span>
                                    Failed to fetch status: ${error.message}
                                `;
                                logSection.appendChild(logEntry);
                                logSection.scrollTop = logSection.scrollHeight;
                            });
                    }
                    
                    function toggleAutoRefresh() {
                        const checkbox = document.getElementById('autoRefresh');
                        if (checkbox.checked) {
                            autoRefreshInterval = setInterval(refreshStatus, 2000);
                        } else {
                            clearInterval(autoRefreshInterval);
                        }
                    }
                    
                    document.getElementById('autoRefresh').addEventListener('change', toggleAutoRefresh);
                    
                    // Initial load
                    refreshStatus();
                    toggleAutoRefresh();
                </script>
            </body>
            </html>
            """;
    }
} 