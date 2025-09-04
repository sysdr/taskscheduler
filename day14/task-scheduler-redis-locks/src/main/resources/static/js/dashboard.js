let chart;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    loadTasks();
    loadStats();
    initChart();
    
    // Auto-refresh every 3 seconds
    setInterval(() => {
        loadTasks();
        loadStats();
        updateChart();
    }, 3000);
});

function createTask() {
    const taskName = document.getElementById('taskName').value.trim();
    if (!taskName) {
        alert('Please enter a task name');
        return;
    }
    
    fetch('/api/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'name=' + encodeURIComponent(taskName)
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('taskName').value = '';
        loadTasks();
        loadStats();
        updateChart();
        console.log('Created task:', data);
    })
    .catch(error => console.error('Error creating task:', error));
}

function createBulkTasks() {
    const promises = [];
    for (let i = 1; i <= 10; i++) {
        const taskName = `Bulk Task ${i} - ${new Date().getTime()}`;
        promises.push(
            fetch('/api/tasks', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'name=' + encodeURIComponent(taskName)
            })
        );
    }
    
    Promise.all(promises)
        .then(() => {
            loadTasks();
            loadStats();
            updateChart();
            console.log('Created 10 bulk tasks');
        })
        .catch(error => console.error('Error creating bulk tasks:', error));
}

function loadTasks() {
    fetch('/api/tasks')
        .then(response => response.json())
        .then(tasks => {
            const tasksList = document.getElementById('tasksList');
            tasksList.innerHTML = '';
            
            tasks.slice(0, 10).forEach(task => {
                const taskElement = document.createElement('div');
                taskElement.className = `task-item ${task.status.toLowerCase()}`;
                
                const executorInfo = task.executorId ? ` (${task.executorId})` : '';
                
                taskElement.innerHTML = `
                    <div>
                        <div class="task-name">${task.name}${executorInfo}</div>
                        <small style="color: #718096;">ID: ${task.id} | Created: ${formatDate(task.createdAt)}</small>
                    </div>
                    <div class="task-status ${task.status.toLowerCase()}">${task.status}</div>
                `;
                
                tasksList.appendChild(taskElement);
            });
        })
        .catch(error => console.error('Error loading tasks:', error));
}

function loadStats() {
    fetch('/api/stats')
        .then(response => response.json())
        .then(stats => {
            document.querySelector('.stats-grid .stat-card:nth-child(1) .stat-value').textContent = stats.totalTasks;
            document.querySelector('.stats-grid .stat-card:nth-child(2) .stat-value').textContent = stats.pendingTasks;
            document.querySelector('.stats-grid .stat-card:nth-child(3) .stat-value').textContent = stats.runningTasks;
            document.querySelector('.stats-grid .stat-card:nth-child(4) .stat-value').textContent = stats.completedTasks;
        })
        .catch(error => console.error('Error loading stats:', error));
}

function initChart() {
    const ctx = document.getElementById('tasksChart').getContext('2d');
    chart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Pending', 'Running', 'Completed', 'Failed'],
            datasets: [{
                data: [0, 0, 0, 0],
                backgroundColor: [
                    '#ed8936',
                    '#4299e1',
                    '#48bb78',
                    '#f56565'
                ],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        usePointStyle: true
                    }
                },
                title: {
                    display: true,
                    text: 'Task Distribution'
                }
            }
        }
    });
}

function updateChart() {
    fetch('/api/stats')
        .then(response => response.json())
        .then(stats => {
            chart.data.datasets[0].data = [
                stats.pendingTasks,
                stats.runningTasks,
                stats.completedTasks,
                stats.failedTasks
            ];
            chart.update('none');
        })
        .catch(error => console.error('Error updating chart:', error));
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleTimeString();
}

// Handle Enter key in task input
document.addEventListener('DOMContentLoaded', function() {
    const taskInput = document.getElementById('taskName');
    if (taskInput) {
        taskInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                createTask();
            }
        });
    }
});
