let statusChart, throughputChart;
let throughputData = [];
let lastCompletedCount = 0;
let updateInterval;

// Initialize charts
function initCharts() {
    const statusCtx = document.getElementById('statusChart').getContext('2d');
    statusChart = new Chart(statusCtx, {
        type: 'doughnut',
        data: {
            labels: ['Pending', 'Processing', 'Completed', 'Failed'],
            datasets: [{
                data: [0, 0, 0, 0],
                backgroundColor: ['#ffc107', '#17a2b8', '#28a745', '#dc3545']
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

    const throughputCtx = document.getElementById('throughputChart').getContext('2d');
    throughputChart = new Chart(throughputCtx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Tasks/sec',
                data: [],
                borderColor: '#667eea',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            },
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
}

// Update statistics
async function updateStats() {
    try {
        const response = await fetch('/api/tasks/stats');
        const stats = await response.json();

        document.getElementById('pendingCount').textContent = stats.pending || 0;
        document.getElementById('processingCount').textContent = stats.processing || 0;
        document.getElementById('completedCount').textContent = stats.completed || 0;
        document.getElementById('failedCount').textContent = stats.failed || 0;
        document.getElementById('queueSize').textContent = stats.queueSize || 0;
        document.getElementById('avgTime').textContent = Math.round(stats.avgProcessingTimeMs || 0);

        // Update status chart
        statusChart.data.datasets[0].data = [
            stats.pending || 0,
            stats.processing || 0,
            stats.completed || 0,
            stats.failed || 0
        ];
        statusChart.update('none');

        // Calculate throughput
        const currentCompleted = stats.completed || 0;
        const throughput = Math.max(0, (currentCompleted - lastCompletedCount) / 2);
        lastCompletedCount = currentCompleted;

        // Update throughput chart
        const now = new Date().toLocaleTimeString();
        throughputData.push({ time: now, rate: throughput });
        if (throughputData.length > 30) {
            throughputData.shift();
        }

        throughputChart.data.labels = throughputData.map(d => d.time);
        throughputChart.data.datasets[0].data = throughputData.map(d => d.rate);
        throughputChart.update('none');

    } catch (error) {
        console.error('Error updating stats:', error);
    }
}

// Update recent batches
async function updateBatches() {
    try {
        const response = await fetch('/api/tasks/batches');
        const batches = await response.json();

        const batchList = document.getElementById('batchList');
        
        if (batches.length === 0) {
            batchList.innerHTML = '<div class="loading">No batches processed yet</div>';
            return;
        }

        batchList.innerHTML = batches.slice(0, 20).map(batch => `
            <div class="batch-item">
                <div class="batch-info">
                    <label>Batch ID</label>
                    <value>${batch.batchId.substring(0, 8)}...</value>
                </div>
                <div class="batch-info">
                    <label>Size</label>
                    <value>${batch.batchSize}</value>
                </div>
                <div class="batch-info">
                    <label>Success</label>
                    <value style="color: #28a745">${batch.successCount}</value>
                </div>
                <div class="batch-info">
                    <label>Failed</label>
                    <value style="color: #dc3545">${batch.failureCount}</value>
                </div>
                <div class="batch-info">
                    <label>Duration</label>
                    <value>${batch.totalProcessingTimeMs}ms</value>
                </div>
                <div class="batch-info">
                    <label>Avg/Task</label>
                    <value>${Math.round(batch.avgTaskProcessingTimeMs)}ms</value>
                </div>
            </div>
        `).join('');

    } catch (error) {
        console.error('Error updating batches:', error);
    }
}

// Create batch of tasks
async function createBatchTasks() {
    const count = parseInt(document.getElementById('taskCount').value);
    const taskTypesSelect = document.getElementById('taskTypes');
    const selectedTypes = Array.from(taskTypesSelect.selectedOptions).map(option => option.value);

    if (selectedTypes.length === 0) {
        alert('Please select at least one task type');
        return;
    }

    try {
        const response = await fetch('/api/tasks/batch', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                count: count,
                taskTypes: selectedTypes
            })
        });

        const result = await response.json();
        console.log('Created tasks:', result);
        
        // Update stats immediately
        setTimeout(updateStats, 500);
        setTimeout(updateBatches, 1000);

    } catch (error) {
        console.error('Error creating tasks:', error);
        alert('Error creating tasks');
    }
}

// Initialize dashboard
document.addEventListener('DOMContentLoaded', () => {
    initCharts();
    updateStats();
    updateBatches();
    
    // Update every 2 seconds
    updateInterval = setInterval(() => {
        updateStats();
    }, 2000);
    
    // Update batches every 5 seconds
    setInterval(updateBatches, 5000);
});
