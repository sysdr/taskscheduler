const API_BASE = 'http://localhost:8080/api';

// Helper function to get fresh token from localStorage
function getToken() {
    return localStorage.getItem('token');
}

// Check authentication on page load
if (!getToken()) {
    window.location.href = '/login';
}

// Display username
const username = localStorage.getItem('username');
document.getElementById('userDisplay').textContent = `👤 ${username}`;

// Helper function to safely read error response (handles already-read streams)
async function readErrorResponse(response) {
    try {
        // Check if body is already consumed
        if (response.bodyUsed) {
            return `Error (Status ${response.status})`;
        }
        
        const text = await response.text();
        if (!text || text.trim() === '') {
            return `Error (Status ${response.status})`;
        }
        
        try {
            const json = JSON.parse(text);
            return json.message || text || `Error (Status ${response.status})`;
        } catch (e) {
            return text || `Error (Status ${response.status})`;
        }
    } catch (e) {
        // If we can't read the body (already consumed or other error), return a generic message
        console.error('Error reading response:', e);
        return `Error (Status ${response.status})`;
    }
}

// Helper function to check for auth errors and logout if needed
function handleAuthError(response) {
    if (response.status === 401 || response.status === 403) {
        console.error(`Authentication error (${response.status}): Token may be invalid or expired.`);
        console.error('Redirecting to login page...');
        logout();
        return true;
    }
    return false;
}

// Load tasks and metrics on page load
loadTasks();
loadMetrics();

async function loadTasks() {
    const token = getToken();
    if (!token) {
        logout();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/tasks/my`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) {
            return;
        }
        
        if (!response.ok) {
            const errorMessage = await readErrorResponse(response) || 'Failed to load tasks';
            console.error('Load tasks error:', response.status, errorMessage);
            return;
        }
        
        const tasks = await response.json();
        displayTasks(tasks);
    } catch (error) {
        console.error('Failed to load tasks:', error);
    }
}

function displayTasks(tasks) {
    const grid = document.getElementById('tasksGrid');
    
    if (tasks.length === 0) {
        grid.innerHTML = '<div class="feature-card"><h3>No tasks yet</h3><p>Create your first task to get started!</p></div>';
        return;
    }
    
    grid.innerHTML = tasks.map(task => {
        const isActive = task.status === 'ACTIVE';
        const statusIcon = isActive ? '🟢' : (task.status === 'FAILED' ? '🔴' : '⚪');
        const statusText = isActive ? 'ACTIVE' : (task.status === 'FAILED' ? 'FAILED' : 'INACTIVE');
        const borderColor = isActive ? '#10b981' : (task.status === 'FAILED' ? '#ef4444' : '#6b7280');
        
        return `
        <div class="task-card" style="border-left-color: ${borderColor};">
            <div class="task-header">
                <div class="task-title-wrapper">
                    <div class="task-title">${task.name}</div>
                    <div class="task-status-badge status-${task.status.toLowerCase()}">
                        ${statusIcon} ${statusText}
                    </div>
                </div>
            </div>
            <div class="task-description">${task.description || 'No description'}</div>
            <div class="task-meta">
                <div><strong>Status:</strong> <span class="status-text status-${task.status.toLowerCase()}">${statusText}</span></div>
                <div>📅 Cron: ${task.cronExpression}</div>
                <div>🔢 Executions: ${task.executionCount}</div>
                ${task.lastExecuted ? `<div>⏱️ Last run: ${new Date(task.lastExecuted).toLocaleString()}</div>` : ''}
            </div>
            <div class="task-actions">
                ${task.status === 'ACTIVE' || task.status === 'INACTIVE' ? 
                    `<button onclick="toggleTaskStatus(${task.id})" class="btn-sm ${isActive ? 'btn-warning' : 'btn-success'}">
                        ${isActive ? '⏸️ Deactivate' : '▶️ Activate'}
                    </button>` : ''
                }
                <button onclick="executeTask(${task.id})" class="btn-sm btn-success">▶️ Execute</button>
                <button onclick="deleteTask(${task.id})" class="btn-sm btn-danger">🗑️ Delete</button>
            </div>
        </div>
        `;
    }).join('');
}

async function executeTask(taskId) {
    const token = getToken();
    if (!token) {
        logout();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/tasks/${taskId}/execute`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) {
            return;
        }
        
        if (response.ok) {
            alert('Task executed successfully!');
            loadTasks();
            loadMetrics();
        } else {
            const errorMessage = await readErrorResponse(response) || 'Failed to execute task';
            console.error('Execute task error:', response.status, errorMessage);
            alert(errorMessage);
        }
    } catch (error) {
        console.error('Execution failed:', error);
        alert('Failed to execute task: ' + (error.message || 'Unknown error'));
    }
}

async function deleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) {
        return;
    }
    
    const token = getToken();
    if (!token) {
        logout();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/tasks/${taskId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) {
            return;
        }
        
        if (!response.ok) {
            const errorMessage = await readErrorResponse(response) || 'Failed to delete task';
            console.error('Delete task error:', response.status, errorMessage);
            alert(errorMessage);
            return;
        }
        
        alert('Task deleted successfully!');
        loadTasks();
        loadMetrics();
    } catch (error) {
        console.error('Deletion failed:', error);
        alert('Failed to delete task: ' + (error.message || 'Unknown error'));
    }
}

async function toggleTaskStatus(taskId) {
    const token = getToken();
    if (!token) {
        logout();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/tasks/${taskId}/toggle-status`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) {
            return;
        }
        
        if (!response.ok) {
            const errorMessage = await readErrorResponse(response) || 'Failed to toggle task status';
            console.error('Toggle status error:', response.status, errorMessage);
            alert(errorMessage);
            return;
        }
        
        const task = await response.json();
        const statusText = task.status === 'ACTIVE' ? 'activated' : 'deactivated';
        alert(`Task ${statusText} successfully!`);
        loadTasks();
        loadMetrics();
    } catch (error) {
        console.error('Toggle failed:', error);
        alert('Failed to toggle task status: ' + (error.message || 'Unknown error'));
    }
}

function showCreateModal() {
    document.getElementById('createModal').style.display = 'flex';
}

function closeCreateModal() {
    document.getElementById('createModal').style.display = 'none';
}

document.getElementById('createTaskForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const token = getToken();
    if (!token) {
        logout();
        return;
    }
    
    const taskData = {
        name: document.getElementById('taskName').value,
        description: document.getElementById('taskDescription').value,
        cronExpression: document.getElementById('cronExpression').value
    };
    
    try {
        const response = await fetch(`${API_BASE}/tasks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(taskData)
        });
        
        if (handleAuthError(response)) {
            return;
        }
        
        if (response.ok) {
            closeCreateModal();
            document.getElementById('createTaskForm').reset();
            loadTasks();
            loadMetrics();
        } else {
            const errorMessage = await readErrorResponse(response) || 'Failed to create task';
            console.error('Create task error:', response.status, errorMessage);
            alert(errorMessage);
        }
    } catch (error) {
        console.error('Creation failed:', error);
        alert('Failed to create task: ' + (error.message || 'Unknown error'));
    }
});

async function loadMetrics() {
    const token = getToken();
    if (!token) {
        logout();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/metrics`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) {
            return;
        }
        
        if (!response.ok) {
            const errorMessage = await readErrorResponse(response) || 'Failed to load metrics';
            console.error('Metrics API error:', response.status, errorMessage);
            updateMetricsDisplay({});
            return;
        }
        
        const metrics = await response.json();
        console.log('Metrics loaded:', metrics);
        updateMetricsDisplay(metrics);
    } catch (error) {
        console.error('Failed to load metrics:', error);
        updateMetricsDisplay({});
    }
}

function updateMetricsDisplay(metrics) {
    document.getElementById('metricTotalTasks').textContent = metrics.totalTasks || 0;
    document.getElementById('metricActiveTasks').textContent = metrics.activeTasks || 0;
    document.getElementById('metricInactiveTasks').textContent = metrics.inactiveTasks || 0;
    document.getElementById('metricTotalExecutions').textContent = metrics.totalExecutions || 0;
    document.getElementById('metricSuccessfulExecutions').textContent = metrics.successfulExecutions || 0;
    document.getElementById('metricFailedExecutions').textContent = metrics.failedExecutions || 0;
    document.getElementById('metricTotalUsers').textContent = metrics.totalUsers || 0;
    document.getElementById('metricRecentExecutions').textContent = metrics.recentExecutions || 0;
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('roles');
    window.location.href = '/login';
}

// Ensure button handlers are globally accessible for inline onclick handlers
// This is important for buttons created dynamically in displayTasks()
window.executeTask = executeTask;
window.deleteTask = deleteTask;
window.toggleTaskStatus = toggleTaskStatus;
window.showCreateModal = showCreateModal;
window.closeCreateModal = closeCreateModal;
