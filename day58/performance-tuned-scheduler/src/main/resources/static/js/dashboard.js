let currentMode = 'baseline';
let taskCount = 0;
let startTime = Date.now();

// Mode selection
document.querySelectorAll('.mode-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.mode-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        currentMode = btn.dataset.mode;
        console.log('Switched to mode:', currentMode);
    });
});

// Submit single task
document.getElementById('submitTask').addEventListener('click', async () => {
    try {
        const response = await fetch(`/api/tasks/${currentMode}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: `Task-${++taskCount}`,
                payload: `Payload for task ${taskCount}`
            })
        });
        
        if (response.ok) {
            console.log('Task submitted successfully');
            updateMetrics();
        }
    } catch (error) {
        console.error('Error submitting task:', error);
    }
});

// Run load test
document.getElementById('loadTest').addEventListener('click', async () => {
    const btn = document.getElementById('loadTest');
    btn.disabled = true;
    btn.textContent = 'Running...';
    
    try {
        const response = await fetch(`/api/load-test/${currentMode}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            console.log('Load test started');
        }
    } catch (error) {
        console.error('Error running load test:', error);
    } finally {
        setTimeout(() => {
            btn.disabled = false;
            btn.textContent = 'Run Load Test (100 tasks)';
        }, 2000);
    }
});

// Clear metrics
document.getElementById('clearMetrics').addEventListener('click', () => {
    taskCount = 0;
    startTime = Date.now();
    updateMetrics();
});

// Update metrics
async function updateMetrics() {
    try {
        const response = await fetch('/api/metrics');
        const data = await response.json();
        
        document.getElementById('tasksProcessed').textContent = data.tasksProcessed || 0;
        document.getElementById('activeTasks').textContent = data.activeTasks || 0;
        document.getElementById('heapUsed').textContent = data.heapUsedMB || 0;
        
        // Calculate throughput
        const elapsed = (Date.now() - startTime) / 1000;
        const throughput = elapsed > 0 ? Math.round((data.tasksProcessed || 0) / elapsed) : 0;
        document.getElementById('throughput').textContent = throughput;
        
    } catch (error) {
        console.error('Error fetching metrics:', error);
    }
}

// Simple chart visualization
const canvas = document.getElementById('performanceChart');
const ctx = canvas.getContext('2d');
const dataPoints = [];
const maxPoints = 50;

function drawChart() {
    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;
    
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    if (dataPoints.length < 2) return;
    
    const maxValue = Math.max(...dataPoints, 10);
    const xStep = canvas.width / (maxPoints - 1);
    const yScale = (canvas.height - 40) / maxValue;
    
    // Draw grid
    ctx.strokeStyle = '#e0e0e0';
    ctx.lineWidth = 1;
    for (let i = 0; i < 5; i++) {
        const y = 20 + (canvas.height - 40) * i / 4;
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(canvas.width, y);
        ctx.stroke();
    }
    
    // Draw line
    ctx.strokeStyle = '#667eea';
    ctx.lineWidth = 3;
    ctx.beginPath();
    
    dataPoints.forEach((value, index) => {
        const x = index * xStep;
        const y = canvas.height - 20 - (value * yScale);
        
        if (index === 0) {
            ctx.moveTo(x, y);
        } else {
            ctx.lineTo(x, y);
        }
    });
    
    ctx.stroke();
    
    // Draw points
    ctx.fillStyle = '#764ba2';
    dataPoints.forEach((value, index) => {
        const x = index * xStep;
        const y = canvas.height - 20 - (value * yScale);
        ctx.beginPath();
        ctx.arc(x, y, 4, 0, Math.PI * 2);
        ctx.fill();
    });
}

async function updateChart() {
    try {
        const response = await fetch('/api/metrics');
        const data = await response.json();
        
        const throughput = data.tasksProcessed || 0;
        dataPoints.push(throughput);
        
        if (dataPoints.length > maxPoints) {
            dataPoints.shift();
        }
        
        drawChart();
    } catch (error) {
        console.error('Error updating chart:', error);
    }
}

// Auto-refresh
setInterval(() => {
    updateMetrics();
    updateChart();
}, 2000);

// Initial load
updateMetrics();
updateChart();
