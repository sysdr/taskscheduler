// Dashboard real-time updates
let threadUtilizationChart;
let taskTimelineChart;

document.addEventListener('DOMContentLoaded', function() {
    initializeCharts();
    startRealTimeUpdates();
});

function initializeCharts() {
    // Thread Utilization Chart
    const threadCtx = document.getElementById('threadUtilizationChart').getContext('2d');
    threadUtilizationChart = new Chart(threadCtx, {
        type: 'doughnut',
        data: {
            labels: ['Active Threads', 'Idle Threads'],
            datasets: [{
                data: [0, 10],
                backgroundColor: ['#74b9ff', '#ddd'],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });

    // Task Timeline Chart
    const timelineCtx = document.getElementById('taskTimelineChart').getContext('2d');
    taskTimelineChart = new Chart(timelineCtx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [
                {
                    label: 'Active Threads',
                    data: [],
                    borderColor: '#74b9ff',
                    backgroundColor: 'rgba(116, 185, 255, 0.1)',
                    tension: 0.4
                },
                {
                    label: 'Queue Size',
                    data: [],
                    borderColor: '#fd79a8',
                    backgroundColor: 'rgba(253, 121, 168, 0.1)',
                    tension: 0.4
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true
                }
            },
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

function startRealTimeUpdates() {
    setInterval(updateDashboard, 2000); // Update every 2 seconds
}

async function updateDashboard() {
    try {
        // Fetch thread pool stats
        const threadPoolResponse = await fetch('/api/thread-pool-stats');
        const threadPoolStats = await threadPoolResponse.json();
        
        // Update stats display
        document.getElementById('activeThreads').textContent = threadPoolStats.activeThreads;
        document.getElementById('poolSize').textContent = threadPoolStats.poolSize;
        document.getElementById('queueSize').textContent = threadPoolStats.queueSize;
        
        // Update thread utilization chart
        const idleThreads = Math.max(0, threadPoolStats.poolSize - threadPoolStats.activeThreads);
        threadUtilizationChart.data.datasets[0].data = [threadPoolStats.activeThreads, idleThreads];
        threadUtilizationChart.update('none');
        
        // Update timeline chart
        const now = new Date().toLocaleTimeString();
        if (taskTimelineChart.data.labels.length > 20) {
            taskTimelineChart.data.labels.shift();
            taskTimelineChart.data.datasets[0].data.shift();
            taskTimelineChart.data.datasets[1].data.shift();
        }
        
        taskTimelineChart.data.labels.push(now);
        taskTimelineChart.data.datasets[0].data.push(threadPoolStats.activeThreads);
        taskTimelineChart.data.datasets[1].data.push(threadPoolStats.queueSize);
        taskTimelineChart.update('none');
        
        // Fetch and update task metrics
        const taskMetricsResponse = await fetch('/api/task-metrics');
        const taskMetrics = await taskMetricsResponse.json();
        
        // Update task cards (simplified - in real app would update each card)
        console.log('Task metrics updated:', taskMetrics);
        
    } catch (error) {
        console.error('Error updating dashboard:', error);
    }
}

// Add some visual feedback when stats update
function highlightUpdate(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.style.transform = 'scale(1.1)';
        setTimeout(() => {
            element.style.transform = 'scale(1)';
        }, 200);
    }
}
