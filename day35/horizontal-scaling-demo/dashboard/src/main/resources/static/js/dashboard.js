let previousCompleted = 0;
let lastUpdateTime = Date.now();

function updateMetrics() {
    console.log('Updating metrics...');
    fetch('/api/metrics')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Metrics data received:', data);
            // Update metrics display
            const producedEl = document.getElementById('totalProduced');
            const completedEl = document.getElementById('totalCompleted');
            const queueEl = document.getElementById('queueDepth');
            const throughputEl = document.getElementById('throughput');
            const demoBanner = document.getElementById('demoBanner');
            
            console.log('Elements found:', {
                producedEl: !!producedEl,
                completedEl: !!completedEl,
                queueEl: !!queueEl,
                throughputEl: !!throughputEl
            });
            
            if (producedEl) {
                producedEl.textContent = data.totalProduced || 0;
                console.log('Set totalProduced to:', data.totalProduced);
            } else {
                console.error('Element totalProduced not found!');
            }
            
            if (completedEl) {
                completedEl.textContent = data.totalCompleted || 0;
                console.log('Set totalCompleted to:', data.totalCompleted);
            } else {
                console.error('Element totalCompleted not found!');
            }
            
            if (queueEl) {
                queueEl.textContent = data.queueDepth || 0;
                console.log('Set queueDepth to:', data.queueDepth);
            } else {
                console.error('Element queueDepth not found!');
            }
            
            if (demoBanner) {
                if (data.demoData) {
                    demoBanner.style.display = 'block';
                    demoBanner.textContent = 'Showing demo metrics while live services initialize. Start RabbitMQ/Redis and producers to view real-time data.';
                } else {
                    demoBanner.style.display = 'none';
                }
            }

            // Calculate throughput
            const currentTime = Date.now();
            const timeDiff = Math.max((currentTime - lastUpdateTime) / 1000, 1);
            const completedDiff = (data.totalCompleted || 0) - previousCompleted;
            const throughput = timeDiff > 0 ? Math.round(completedDiff / timeDiff) : 0;
            
            if (throughputEl) {
                throughputEl.innerHTML = throughput + '<span class="unit">/sec</span>';
                console.log('Set throughput to:', throughput);
            } else {
                console.error('Element throughput not found!');
            }
            
            previousCompleted = data.totalCompleted || 0;
            lastUpdateTime = currentTime;
            
            // Update consumers
            updateConsumers(data.consumers || []);
            updateChart(data.consumers || []);
            
            // Hide any error messages
            const errorDiv = document.getElementById('errorMessage');
            if (errorDiv) {
                errorDiv.style.display = 'none';
            }
        })
        .catch(error => {
            console.error('Error fetching metrics:', error);
            // Show error message on page
            let errorDiv = document.getElementById('errorMessage');
            if (!errorDiv) {
                errorDiv = document.createElement('div');
                errorDiv.id = 'errorMessage';
                errorDiv.style.cssText = 'background: #fee; color: #c33; padding: 10px; margin: 10px; border-radius: 5px; text-align: center;';
                document.querySelector('.container').insertBefore(errorDiv, document.querySelector('.metrics-grid'));
            }
            errorDiv.textContent = 'Error loading metrics: ' + error.message;
            errorDiv.style.display = 'block';
        });
}

function updateConsumers(consumers) {
    const container = document.getElementById('consumersContainer');
    
    if (consumers.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #999;">No active consumers</p>';
        return;
    }
    
    container.innerHTML = consumers.map(consumer => `
        <div class="consumer-card">
            <div class="consumer-id">🔷 ${consumer.id}</div>
            <div class="consumer-stats">
                <div class="stat">
                    <div class="stat-value">${consumer.processed || 0}</div>
                    <div class="stat-label">Processed</div>
                </div>
                <div class="stat">
                    <div class="stat-value">${consumer.failed || 0}</div>
                    <div class="stat-label">Failed</div>
                </div>
            </div>
        </div>
    `).join('');
}

let chartInstance = null;

function updateChart(consumers) {
    const ctx = document.getElementById('distributionChart');
    if (!ctx) return;
    
    const labels = consumers.length > 0 ? consumers.map(c => c.id) : ['No Consumers'];
    const data = consumers.length > 0 ? consumers.map(c => c.processed || 0) : [0];
    
    if (chartInstance) {
        chartInstance.data.labels = labels;
        chartInstance.data.datasets[0].data = data;
        chartInstance.update();
    } else {
        chartInstance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Tasks Processed',
                    data: data,
                    backgroundColor: 'rgba(102, 126, 234, 0.8)',
                    borderColor: 'rgba(102, 126, 234, 1)',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }
}

function produceTasks(count) {
    fetch(`http://localhost:8080/api/producer/batch?count=${count}`, {
        method: 'POST'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('Tasks produced:', data);
        alert('Successfully produced ' + count + ' tasks!');
        updateMetrics();
    })
    .catch(error => {
        console.error('Error producing tasks:', error);
        alert('Error producing tasks: ' + error.message + '\n\nMake sure RabbitMQ is running!');
    });
}

function resetStats() {
    if (confirm('Are you sure you want to reset all statistics?')) {
        // This would require a backend endpoint
        console.log('Reset not implemented yet');
    }
}

// Update metrics every 2 seconds
console.log('Dashboard script loaded, starting metrics updates...');

// Wait for DOM to be fully ready
function initDashboard() {
    console.log('Initializing dashboard...');
    console.log('Document ready state:', document.readyState);
    
    // Check if elements exist
    const testEl = document.getElementById('totalProduced');
    console.log('totalProduced element exists:', !!testEl);
    
    if (!testEl) {
        console.error('DOM elements not found! Retrying in 500ms...');
        setTimeout(initDashboard, 500);
        return;
    }
    
    console.log('DOM is ready, starting metrics updates');
    setInterval(updateMetrics, 2000);
    updateMetrics();
}

// Start initialization
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initDashboard);
} else {
    initDashboard();
}
