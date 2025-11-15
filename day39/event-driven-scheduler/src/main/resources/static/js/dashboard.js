let eventChart, taskChart;
let stompClient = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeCharts();
    loadMetrics();
    loadRecentTasks();
    connectWebSocket();
    
    // Refresh data every 5 seconds
    setInterval(loadMetrics, 5000);
    setInterval(loadRecentTasks, 10000);
});

function initializeCharts() {
    // Event Distribution Chart
    const eventCtx = document.getElementById('eventChart').getContext('2d');
    eventChart = new Chart(eventCtx, {
        type: 'doughnut',
        data: {
            labels: ['File Events', 'User Events', 'System Events'],
            datasets: [{
                data: [0, 0, 0],
                backgroundColor: [
                    'rgba(102, 126, 234, 0.8)',
                    'rgba(240, 147, 251, 0.8)',
                    'rgba(79, 172, 254, 0.8)'
                ],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });

    // Task Status Chart
    const taskCtx = document.getElementById('taskChart').getContext('2d');
    taskChart = new Chart(taskCtx, {
        type: 'bar',
        data: {
            labels: ['Pending', 'Running', 'Completed', 'Failed'],
            datasets: [{
                label: 'Tasks',
                data: [0, 0, 0, 0],
                backgroundColor: [
                    'rgba(255, 243, 205, 0.8)',
                    'rgba(207, 226, 255, 0.8)',
                    'rgba(209, 231, 221, 0.8)',
                    'rgba(248, 215, 218, 0.8)'
                ],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

function loadMetrics() {
    fetch('/api/tasks/metrics')
        .then(response => response.json())
        .then(data => {
            document.getElementById('fileEvents').textContent = data.fileEventsReceived;
            document.getElementById('userEvents').textContent = data.userEventsReceived;
            document.getElementById('systemEvents').textContent = data.systemEventsReceived;
            document.getElementById('tasksTriggered').textContent = data.tasksTriggered;
            document.getElementById('tasksCompleted').textContent = data.tasksCompleted;
            document.getElementById('tasksFailed').textContent = data.tasksFailed;
            
            // Update event chart
            eventChart.data.datasets[0].data = [
                data.fileEventsReceived,
                data.userEventsReceived,
                data.systemEventsReceived
            ];
            eventChart.update();
        })
        .catch(error => console.error('Error loading metrics:', error));

    // Load task stats for chart
    fetch('/api/tasks/stats')
        .then(response => response.json())
        .then(data => {
            taskChart.data.datasets[0].data = [
                data.pendingTasks,
                data.runningTasks,
                data.completedTasks,
                data.failedTasks
            ];
            taskChart.update();
        })
        .catch(error => console.error('Error loading stats:', error));
}

function loadRecentTasks() {
    fetch('/api/tasks/recent')
        .then(response => response.json())
        .then(tasks => {
            const tbody = document.getElementById('tasksTableBody');
            
            if (tasks.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="no-data">No tasks yet</td></tr>';
                return;
            }
            
            tbody.innerHTML = tasks.map(task => `
                <tr>
                    <td>${task.taskName}</td>
                    <td>${task.taskType}</td>
                    <td><span class="status-badge status-${task.status.toLowerCase()}">${task.status}</span></td>
                    <td>${task.eventType || 'N/A'}</td>
                    <td>${task.executionTimeMs || 0}ms</td>
                    <td>${task.result ? task.result.substring(0, 50) : 'Pending...'}</td>
                </tr>
            `).join('');
        })
        .catch(error => console.error('Error loading tasks:', error));
}

function triggerFileUpload() {
    fetch('/api/events/demo/file-upload', { method: 'POST' })
        .then(response => response.json())
        .then(data => {
            addLogEntry('FILE_UPLOAD', 'File upload event published');
            setTimeout(loadMetrics, 1000);
        })
        .catch(error => console.error('Error triggering file upload:', error));
}

function triggerUserRegistration() {
    fetch('/api/events/demo/user-registration', { method: 'POST' })
        .then(response => response.json())
        .then(data => {
            addLogEntry('USER_REGISTRATION', 'User registration event published');
            setTimeout(loadMetrics, 1000);
        })
        .catch(error => console.error('Error triggering user registration:', error));
}

function triggerSystemAlert() {
    fetch('/api/events/demo/system-alert', { method: 'POST' })
        .then(response => response.json())
        .then(data => {
            addLogEntry('SYSTEM_ALERT', 'System alert event published');
            setTimeout(loadMetrics, 1000);
        })
        .catch(error => console.error('Error triggering system alert:', error));
}

function generateDemoData() {
    const button = event.target;
    const originalText = button.textContent;
    button.disabled = true;
    button.textContent = '⏳ Generating...';
    
    fetch('/api/tasks/demo/generate?count=50', { method: 'POST' })
        .then(response => response.json())
        .then(data => {
            addLogEntry('DEMO_DATA', `Generated ${data.tasksGenerated} demo tasks`);
            setTimeout(() => {
                loadMetrics();
                loadRecentTasks();
                button.disabled = false;
                button.textContent = originalText;
            }, 1000);
        })
        .catch(error => {
            console.error('Error generating demo data:', error);
            button.disabled = false;
            button.textContent = originalText;
            alert('Error generating demo data. Please try again.');
        });
}

function connectWebSocket() {
    // WebSocket connection for real-time updates (future enhancement)
    console.log('WebSocket connection ready');
}

function addLogEntry(eventType, message) {
    const logContent = document.getElementById('eventLogContent');
    const timestamp = new Date().toLocaleTimeString();
    
    const entry = document.createElement('div');
    entry.className = 'log-entry';
    entry.innerHTML = `
        <span class="log-time">[${timestamp}]</span>
        <span class="log-event">${eventType}</span>: ${message}
    `;
    
    logContent.insertBefore(entry, logContent.firstChild);
    
    // Keep only last 50 entries
    while (logContent.children.length > 50) {
        logContent.removeChild(logContent.lastChild);
    }
}
