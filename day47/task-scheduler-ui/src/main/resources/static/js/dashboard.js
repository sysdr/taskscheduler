// Task Scheduler Dashboard JavaScript

let currentFilter = 'all';
let eventSource = null;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', () => {
    loadTasks();
    loadMetrics();
    setupEventSource();
    setupFilters();
    setupForm();
});

// Load tasks from API
async function loadTasks() {
    try {
        const response = await fetch('/api/tasks');
        const tasks = await response.json();
        renderTasks(tasks);
    } catch (error) {
        console.error('Error loading tasks:', error);
        showError('Failed to load tasks');
    }
}

// Load metrics
async function loadMetrics() {
    try {
        const response = await fetch('/api/tasks/metrics');
        const metrics = await response.json();
        updateMetrics(metrics);
    } catch (error) {
        console.error('Error loading metrics:', error);
    }
}

// Setup Server-Sent Events for real-time updates
function setupEventSource() {
    eventSource = new EventSource('/api/tasks/stream');
    
    eventSource.addEventListener('update', (event) => {
        const data = JSON.parse(event.data);
        renderTasks(data.tasks);
        updateMetrics(data.metrics);
    });
    
    eventSource.onerror = (error) => {
        console.error('SSE Error:', error);
        setTimeout(() => {
            setupEventSource();
        }, 5000);
    };
}

// Render tasks in table
function renderTasks(tasks) {
    const tbody = document.getElementById('tasksTableBody');
    
    // Filter tasks
    const filteredTasks = currentFilter === 'all' 
        ? tasks 
        : tasks.filter(task => task.status === currentFilter);
    
    if (filteredTasks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">No tasks found</td></tr>';
        return;
    }
    
    tbody.innerHTML = filteredTasks.map(task => `
        <tr>
            <td>
                <strong>${task.name}</strong>
                <div style="font-size: 0.75rem; color: var(--text-dim); margin-top: 0.25rem;">
                    ${task.description || ''}
                </div>
            </td>
            <td>${formatTaskType(task.type)}</td>
            <td>
                <span class="status-badge status-${task.status}">
                    ${task.status}
                </span>
            </td>
            <td>${formatDateTime(task.lastExecution)}</td>
            <td>
                <div>${task.executionCount || 0} runs</div>
                <div style="font-size: 0.75rem; color: var(--text-dim);">
                    ${task.failureCount || 0} failed
                </div>
            </td>
            <td>
                <div class="action-buttons">
                    ${task.status !== 'RUNNING' ? `
                        <button class="btn btn-sm btn-success" onclick="executeTask(${task.id})">
                            Run
                        </button>
                    ` : ''}
                    <button class="btn btn-sm btn-danger" onclick="deleteTask(${task.id})">
                        Delete
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Update metrics display
function updateMetrics(metrics) {
    document.getElementById('totalTasks').textContent = metrics.totalTasks || 0;
    document.getElementById('successRate').textContent = 
        (metrics.successRate || 0).toFixed(1) + '%';
    document.getElementById('avgTime').textContent = 
        (metrics.avgExecutionTimeMs || 0) + 'ms';
    document.getElementById('failedTasks').textContent = metrics.failedTasks || 0;
}

// Format task type
function formatTaskType(type) {
    const types = {
        'CRON': '🕐 Cron',
        'FIXED_DELAY': '⏱️ Fixed Delay',
        'ONE_TIME': '▶️ One Time'
    };
    return types[type] || type;
}

// Format date time
function formatDateTime(dateTime) {
    if (!dateTime) return 'Never';
    const date = new Date(dateTime);
    return date.toLocaleString();
}

// Setup filter buttons
function setupFilters() {
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.filter-btn').forEach(b => 
                b.classList.remove('active'));
            btn.classList.add('active');
            currentFilter = btn.dataset.filter;
            loadTasks();
        });
    });
}

// Setup form submission
function setupForm() {
    const form = document.getElementById('createTaskForm');
    const submitBtn = form.querySelector('button[type="submit"]');
    
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        // Disable submit button
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Creating...';
        }
        
        const formData = new FormData(e.target);
        const task = {
            name: formData.get('name')?.trim(),
            description: formData.get('description')?.trim(),
            type: formData.get('type'),
            status: 'SCHEDULED'
        };
        
        // Validate required fields
        if (!task.name) {
            showError('Task name is required');
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Create Task';
            }
            return;
        }
        
        if (task.type === 'CRON') {
            const cronExpr = formData.get('cronExpression')?.trim();
            if (!cronExpr) {
                showError('Cron expression is required for CRON tasks');
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.textContent = 'Create Task';
                }
                return;
            }
            task.cronExpression = cronExpr;
        } else if (task.type === 'FIXED_DELAY') {
            const delay = formData.get('fixedDelayMs');
            if (!delay || parseInt(delay) <= 0) {
                showError('Fixed delay must be a positive number');
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.textContent = 'Create Task';
                }
                return;
            }
            task.fixedDelayMs = parseInt(delay);
        }
        
        try {
            const response = await fetch('/api/tasks', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(task)
            });
            
            if (response.ok) {
                const createdTask = await response.json();
                closeCreateModal();
                e.target.reset();
                showSuccess(`Task "${createdTask.name}" created successfully`);
                // Reload tasks and metrics after creation
                loadTasks();
                loadMetrics();
            } else {
                const errorText = await response.text();
                showError(`Failed to create task: ${errorText || response.statusText}`);
            }
        } catch (error) {
            console.error('Error creating task:', error);
            showError('Failed to create task: ' + error.message);
        } finally {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Create Task';
            }
        }
    });
}

// Execute task
async function executeTask(taskId) {
    // Disable button during execution
    const buttons = document.querySelectorAll(`button[onclick="executeTask(${taskId})"]`);
    buttons.forEach(btn => {
        btn.disabled = true;
        btn.textContent = 'Running...';
    });
    
    try {
        const response = await fetch(`/api/tasks/${taskId}/execute`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showSuccess('Task execution started');
            // Reload tasks and metrics after a short delay
            setTimeout(() => {
                loadTasks();
                loadMetrics();
            }, 500);
        } else {
            const errorText = await response.text();
            showError(`Failed to execute task: ${errorText || response.statusText}`);
            // Re-enable button on error
            buttons.forEach(btn => {
                btn.disabled = false;
                btn.textContent = 'Run';
            });
        }
    } catch (error) {
        console.error('Error executing task:', error);
        showError('Failed to execute task: ' + error.message);
        // Re-enable button on error
        buttons.forEach(btn => {
            btn.disabled = false;
            btn.textContent = 'Run';
        });
    }
}

// Execute next scheduled task
async function executeNextTask() {
    const btn = document.querySelector('button[onclick*="executeNextTask"]');
    const originalText = btn ? btn.textContent : '⏭️ Next Task';
    
    if (btn) {
        btn.disabled = true;
        btn.textContent = '⏳ Executing...';
    }
    
    try {
        const response = await fetch('/api/tasks/next/execute', {
            method: 'POST'
        });
        
        if (response.ok) {
            const task = await response.json();
            showSuccess(`Next task "${task.name}" execution started`);
            // Reload tasks and metrics after a short delay
            setTimeout(() => {
                loadTasks();
                loadMetrics();
                if (btn) {
                    btn.disabled = false;
                    btn.textContent = originalText;
                }
            }, 1000);
        } else if (response.status === 404) {
            const errorData = await response.json().catch(() => ({ message: 'No scheduled tasks available' }));
            showError(errorData.message || 'No scheduled tasks available');
            if (btn) {
                btn.disabled = false;
                btn.textContent = originalText;
            }
        } else {
            const errorText = await response.text();
            showError(`Failed to execute next task: ${errorText || response.statusText}`);
            if (btn) {
                btn.disabled = false;
                btn.textContent = originalText;
            }
        }
    } catch (error) {
        console.error('Error executing next task:', error);
        showError('Failed to execute next task: ' + error.message);
        if (btn) {
            btn.disabled = false;
            btn.textContent = originalText;
        }
    }
}

// Delete task
async function deleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/tasks/${taskId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showSuccess('Task deleted successfully');
            // Reload tasks and metrics immediately
            loadTasks();
            loadMetrics();
        } else {
            const errorText = await response.text();
            showError(`Failed to delete task: ${errorText || response.statusText}`);
        }
    } catch (error) {
        console.error('Error deleting task:', error);
        showError('Failed to delete task: ' + error.message);
    }
}

// Modal functions
function showCreateModal() {
    document.getElementById('createModal').classList.add('show');
}

function closeCreateModal() {
    document.getElementById('createModal').classList.remove('show');
}

function toggleScheduleFields(type) {
    document.getElementById('cronField').style.display = 
        type === 'CRON' ? 'block' : 'none';
    document.getElementById('delayField').style.display = 
        type === 'FIXED_DELAY' ? 'block' : 'none';
}

// Notification functions
function showSuccess(message) {
    console.log('✅', message);
    showToast(message, 'success');
}

function showError(message) {
    console.error('❌', message);
    showToast(message, 'error');
}

// Toast notification system
function showToast(message, type = 'info') {
    // Remove existing toasts
    const existingToasts = document.querySelectorAll('.toast');
    existingToasts.forEach(toast => toast.remove());
    
    // Create toast element
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    
    // Add styles
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 24px;
        background: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3b82f6'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        z-index: 10000;
        animation: slideIn 0.3s ease-out;
        font-size: 14px;
        max-width: 400px;
    `;
    
    // Add animation
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
    `;
    if (!document.querySelector('style[data-toast]')) {
        style.setAttribute('data-toast', 'true');
        document.head.appendChild(style);
    }
    
    document.body.appendChild(toast);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        toast.style.animation = 'slideIn 0.3s ease-out reverse';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Close modal on outside click
window.addEventListener('click', (e) => {
    const modal = document.getElementById('createModal');
    if (e.target === modal) {
        closeCreateModal();
    }
});
