let rateLimiterEnabled = true;
let chart;
let dataPoints = [];
const maxDataPoints = 30;

// Initialize chart
document.addEventListener('DOMContentLoaded', function() {
    const ctx = document.getElementById('throughputChart').getContext('2d');
    chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Tasks Processed per Second',
                data: [],
                borderColor: '#667eea',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                },
                title: {
                    display: true,
                    text: 'Real-time Throughput Monitor',
                    font: { size: 18 }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Tasks/Second'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Time'
                    }
                }
            }
        }
    });
    
    updateMetrics();
    setInterval(updateMetrics, 1000);
});

async function submitSingleTask() {
    const task = {
        type: 'email',
        payload: 'Single task at ' + new Date().toLocaleTimeString()
    };
    
    try {
        const response = await fetch('/api/tasks', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(task)
        });
        const data = await response.json();
        addLog('Submitted task: ' + data.taskId);
    } catch (error) {
        addLog('Error: ' + error.message, true);
    }
}

async function submitBurst() {
    const size = document.getElementById('burstSize').value;
    addLog(`🚀 Submitting burst of ${size} tasks...`, false, true);
    
    try {
        const response = await fetch(`/api/tasks/burst?count=${size}`, {
            method: 'POST'
        });
        const data = await response.json();
        addLog(`✓ Burst submitted: ${data.count} tasks`, false, true);
    } catch (error) {
        addLog('Error: ' + error.message, true);
    }
}

async function toggleRateLimiter() {
    try {
        const response = await fetch('/api/tasks/rate-limiter/toggle', {
            method: 'POST'
        });
        const data = await response.json();
        rateLimiterEnabled = data.enabled;
        
        const btn = document.getElementById('rateLimiterBtn');
        const status = document.getElementById('rateLimiterStatus');
        
        if (rateLimiterEnabled) {
            btn.textContent = 'Disable Rate Limiting';
            status.textContent = 'Status: Enabled (10 tasks/sec)';
            addLog('⚡ Rate limiting enabled');
        } else {
            btn.textContent = 'Enable Rate Limiting';
            status.textContent = 'Status: Disabled (No limit)';
            addLog('⚠️ Rate limiting disabled - Watch for resource spike!');
        }
    } catch (error) {
        addLog('Error: ' + error.message, true);
    }
}

async function updateMetrics() {
    try {
        const response = await fetch('/api/tasks/metrics');
        const data = await response.json();
        
        document.getElementById('processedCount').textContent = data.processed;
        document.getElementById('throttledCount').textContent = data.throttled;
        
        // Update chart
        const now = new Date().toLocaleTimeString();
        chart.data.labels.push(now);
        
        // Calculate rate from last update
        const lastCount = dataPoints.length > 0 ? dataPoints[dataPoints.length - 1] : 0;
        const currentRate = Math.max(0, data.processed - lastCount);
        
        chart.data.datasets[0].data.push(currentRate);
        dataPoints.push(data.processed);
        document.getElementById('currentRate').textContent = currentRate;
        
        if (chart.data.labels.length > maxDataPoints) {
            chart.data.labels.shift();
            chart.data.datasets[0].data.shift();
            dataPoints.shift();
        }
        
        chart.update('none');
    } catch (error) {
        console.error('Failed to update metrics:', error);
    }
}

function addLog(message, isError = false, isBurst = false) {
    const logContainer = document.getElementById('logContainer');
    const entry = document.createElement('div');
    entry.className = 'log-entry' + (isBurst ? ' burst' : '');
    entry.style.color = isError ? '#e53e3e' : '#4a5568';
    
    const timestamp = new Date().toLocaleTimeString();
    entry.textContent = `[${timestamp}] ${message}`;
    
    logContainer.insertBefore(entry, logContainer.firstChild);
    
    // Keep only last 50 entries
    while (logContainer.children.length > 50) {
        logContainer.removeChild(logContainer.lastChild);
    }
}
