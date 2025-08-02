// TaskScheduler Pro - Enterprise Dashboard JavaScript

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 TaskScheduler Pro Dashboard Loaded');
    
    // Initialize all dashboard components
    initializeSidebar();
    initializeCharts();
    initializeInteractiveFeatures();
    initializeRealTimeUpdates();
    
    // Add some professional touches
    addProfessionalFeatures();
});

// Sidebar functionality
function initializeSidebar() {
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.querySelector('.sidebar');
    
    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', function() {
            sidebar.classList.toggle('open');
        });
        
        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', function(e) {
            if (window.innerWidth <= 768) {
                if (!sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
                    sidebar.classList.remove('open');
                }
            }
        });
    }
    
    // Enhanced navigation functionality
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remove active class from all nav items
            navItems.forEach(nav => nav.classList.remove('active'));
            
            // Add active class to clicked item
            this.classList.add('active');
            
            // Get the target section from href
            const target = this.querySelector('.nav-link').getAttribute('href').substring(1);
            
            // Navigate to the appropriate section
            navigateToSection(target);
            
            // Update header title
            updateHeaderTitle(target);
        });
    });
    
    // Initialize with dashboard view
    navigateToSection('dashboard');
}

// Navigation function to handle different sections
function navigateToSection(section) {
    const dashboardContent = document.querySelector('.dashboard-content');
    
    switch(section) {
        case 'dashboard':
            showDashboardView();
            break;
        case 'tasks':
            showTasksView();
            break;
        case 'schedules':
            showSchedulesView();
            break;
        case 'executions':
            showExecutionsView();
            break;
        case 'monitoring':
            showMonitoringView();
            break;
        case 'settings':
            showSettingsView();
            break;
        default:
            showDashboardView();
    }
}

// Update header title based on current section
function updateHeaderTitle(section) {
    const headerTitle = document.querySelector('.header-left h1');
    if (!headerTitle) return;
    
    const titles = {
        'dashboard': 'Dashboard Overview',
        'tasks': 'Task Management',
        'schedules': 'Schedule Configuration',
        'executions': 'Execution History',
        'monitoring': 'System Monitoring',
        'settings': 'Application Settings'
    };
    
    headerTitle.textContent = titles[section] || 'Dashboard Overview';
}

// Show dashboard view (default view)
function showDashboardView() {
    const dashboardContent = document.querySelector('.dashboard-content');
    if (!dashboardContent) return;
    
    dashboardContent.innerHTML = `
        <!-- Key Metrics Cards -->
        <div class="metrics-section">
            <div class="metrics-grid">
                <div class="metric-card">
                    <div class="metric-icon health">
                        <i class="fas fa-heartbeat"></i>
                    </div>
                    <div class="metric-content">
                        <h3>Health Checks</h3>
                        <div class="metric-value">${document.querySelector('.metric-value')?.textContent || '0'}</div>
                        <div class="metric-label">Fixed Rate (5s)</div>
                        <div class="metric-trend positive">
                            <i class="fas fa-arrow-up"></i>
                            <span>+12%</span>
                        </div>
                    </div>
                </div>
                
                <div class="metric-card">
                    <div class="metric-icon cleanup">
                        <i class="fas fa-broom"></i>
                    </div>
                    <div class="metric-content">
                        <h3>Cleanup Tasks</h3>
                        <div class="metric-value">${document.querySelectorAll('.metric-value')[1]?.textContent || '0'}</div>
                        <div class="metric-label">Fixed Delay (15s)</div>
                        <div class="metric-trend positive">
                            <i class="fas fa-arrow-up"></i>
                            <span>+8%</span>
                        </div>
                    </div>
                </div>
                
                <div class="metric-card">
                    <div class="metric-icon reports">
                        <i class="fas fa-file-alt"></i>
                    </div>
                    <div class="metric-content">
                        <h3>Reports Generated</h3>
                        <div class="metric-value">${document.querySelectorAll('.metric-value')[2]?.textContent || '0'}</div>
                        <div class="metric-label">Cron (Every Minute)</div>
                        <div class="metric-trend positive">
                            <i class="fas fa-arrow-up"></i>
                            <span>+15%</span>
                        </div>
                    </div>
                </div>
                
                <div class="metric-card">
                    <div class="metric-icon performance">
                        <i class="fas fa-tachometer-alt"></i>
                    </div>
                    <div class="metric-content">
                        <h3>Avg Response Time</h3>
                        <div class="metric-value">245ms</div>
                        <div class="metric-label">Last 24 hours</div>
                        <div class="metric-trend negative">
                            <i class="fas fa-arrow-down"></i>
                            <span>-3%</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Charts and Analytics -->
        <div class="analytics-section">
            <div class="chart-row">
                <div class="chart-card">
                    <div class="chart-header">
                        <h3>Task Execution Overview</h3>
                        <div class="chart-actions">
                            <button class="btn btn-sm btn-outline">24h</button>
                            <button class="btn btn-sm btn-outline active">7d</button>
                            <button class="btn btn-sm btn-outline">30d</button>
                        </div>
                    </div>
                    <div class="chart-container">
                        <canvas id="executionChart"></canvas>
                    </div>
                </div>
                
                <div class="chart-card">
                    <div class="chart-header">
                        <h3>Task Status Distribution</h3>
                    </div>
                    <div class="chart-container">
                        <canvas id="statusChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Executions Table -->
        <div class="executions-section">
            <div class="section-header">
                <h3>Recent Task Executions</h3>
                <div class="section-actions">
                    <button class="btn btn-primary">
                        <i class="fas fa-plus"></i>
                        New Task
                    </button>
                    <button class="btn btn-outline">
                        <i class="fas fa-download"></i>
                        Export
                    </button>
                </div>
            </div>
            
            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Task Name</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>Start Time</th>
                            <th>Duration</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr class="success-row">
                            <td>
                                <div class="task-info">
                                    <i class="fas fa-clock task-icon"></i>
                                    <span>System Health Check</span>
                                </div>
                            </td>
                            <td>
                                <span class="badge badge-secondary">FIXED_RATE</span>
                            </td>
                            <td>
                                <span class="status-badge success">SUCCESS</span>
                            </td>
                            <td>15:06:37</td>
                            <td>2980ms</td>
                            <td>
                                <div class="action-buttons">
                                    <button class="btn btn-sm btn-icon" title="View Details">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    <button class="btn btn-sm btn-icon" title="Rerun">
                                        <i class="fas fa-redo"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `;
    
    // Reinitialize charts after content update
    setTimeout(() => {
        initializeCharts();
        initializeInteractiveFeatures();
    }, 100);
}

// Show tasks view
function showTasksView() {
    const dashboardContent = document.querySelector('.dashboard-content');
    if (!dashboardContent) return;
    
    dashboardContent.innerHTML = `
        <div class="section-header">
            <h3>Task Management</h3>
            <div class="section-actions">
                <button class="btn btn-primary" onclick="showCreateTaskModal()">
                    <i class="fas fa-plus"></i>
                    Create New Task
                </button>
                <button class="btn btn-outline" onclick="refreshTasks()">
                    <i class="fas fa-refresh"></i>
                    Refresh
                </button>
            </div>
        </div>
        
        <div class="tasks-grid" id="tasksGrid">
            <div class="loading-spinner">Loading tasks...</div>
        </div>
    `;
    
    // Load tasks from API
    loadTasks();
}

// Load tasks from API
async function loadTasks() {
    try {
        const response = await fetch('/api/tasks');
        const tasks = await response.json();
        displayTasks(tasks);
    } catch (error) {
        console.error('Error loading tasks:', error);
        document.getElementById('tasksGrid').innerHTML = '<div class="error-message">Error loading tasks</div>';
    }
}

// Display tasks in the grid
function displayTasks(tasks) {
    const tasksGrid = document.getElementById('tasksGrid');
    if (!tasksGrid) return;
    
    if (tasks.length === 0) {
        tasksGrid.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-tasks"></i>
                <h4>No tasks found</h4>
                <p>Create your first task to get started</p>
                <button class="btn btn-primary" onclick="showCreateTaskModal()">
                    <i class="fas fa-plus"></i>
                    Create Task
                </button>
            </div>
        `;
        return;
    }
    
    const tasksHtml = tasks.map(task => `
        <div class="task-card" data-task-id="${task.id}">
            <div class="task-header">
                <h4>${task.name}</h4>
                <span class="task-status ${task.status.toLowerCase()}">${task.status}</span>
            </div>
            <div class="task-details">
                <p><strong>Description:</strong> ${task.description || 'No description'}</p>
                <p><strong>Type:</strong> ${task.type.displayName}</p>
                <p><strong>Schedule:</strong> ${task.schedule}</p>
                <p><strong>Last Run:</strong> ${task.lastRun ? formatDateTime(task.lastRun) : 'Never'}</p>
                <p><strong>Next Run:</strong> ${task.nextRun ? formatDateTime(task.nextRun) : 'Not scheduled'}</p>
            </div>
            <div class="task-actions">
                <button class="btn btn-sm btn-outline" onclick="editTask('${task.id}')" title="Edit Task">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline" onclick="executeTask('${task.id}')" title="Execute Now">
                    <i class="fas fa-play"></i>
                </button>
                ${task.active ? 
                    `<button class="btn btn-sm btn-outline" onclick="pauseTask('${task.id}')" title="Pause Task">
                        <i class="fas fa-pause"></i>
                    </button>` :
                    `<button class="btn btn-sm btn-outline" onclick="resumeTask('${task.id}')" title="Resume Task">
                        <i class="fas fa-play"></i>
                    </button>`
                }
                <button class="btn btn-sm btn-outline" onclick="deleteTask('${task.id}')" title="Delete Task">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>
    `).join('');
    
    tasksGrid.innerHTML = tasksHtml;
}

// Show create task modal
function showCreateTaskModal() {
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3>Create New Task</h3>
                <button class="modal-close" onclick="closeModal()">&times;</button>
            </div>
            <div class="modal-body">
                <form id="createTaskForm">
                    <div class="form-group">
                        <label for="taskName">Task Name *</label>
                        <input type="text" id="taskName" name="name" required class="form-input">
                    </div>
                    <div class="form-group">
                        <label for="taskDescription">Description</label>
                        <textarea id="taskDescription" name="description" class="form-input" rows="3"></textarea>
                    </div>
                    <div class="form-group">
                        <label for="taskType">Task Type *</label>
                        <select id="taskType" name="type" required class="form-select">
                            <option value="">Select task type</option>
                            <option value="FIXED_RATE">Fixed Rate</option>
                            <option value="FIXED_DELAY">Fixed Delay</option>
                            <option value="CRON">Cron Expression</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="taskSchedule">Schedule *</label>
                        <input type="text" id="taskSchedule" name="schedule" required class="form-input" 
                               placeholder="e.g., 5000 (milliseconds) or 0 * * * * * (cron)">
                        <small class="form-help">
                            For Fixed Rate/Delay: Enter milliseconds (e.g., 5000 for 5 seconds)<br>
                            For Cron: Enter cron expression (e.g., 0 * * * * * for every minute)
                        </small>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn btn-outline" onclick="closeModal()">Cancel</button>
                <button class="btn btn-primary" onclick="createTask()">Create Task</button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Add event listener for form submission
    document.getElementById('createTaskForm').addEventListener('submit', function(e) {
        e.preventDefault();
        createTask();
    });
}

// Create task
async function createTask() {
    const form = document.getElementById('createTaskForm');
    const formData = new FormData(form);
    
    const taskData = {
        name: formData.get('name'),
        description: formData.get('description'),
        type: formData.get('type'),
        schedule: formData.get('schedule')
    };
    
    try {
        const response = await fetch('/api/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(taskData)
        });
        
        if (response.ok) {
            const createdTask = await response.json();
            closeModal();
            showNotification('Task created successfully!', 'success');
            loadTasks(); // Refresh the tasks list
        } else {
            showNotification('Error creating task', 'error');
        }
    } catch (error) {
        console.error('Error creating task:', error);
        showNotification('Error creating task', 'error');
    }
}

// Edit task
async function editTask(taskId) {
    try {
        const response = await fetch(`/api/tasks/${taskId}`);
        const task = await response.json();
        
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Edit Task</h3>
                    <button class="modal-close" onclick="closeModal()">&times;</button>
                </div>
                <div class="modal-body">
                    <form id="editTaskForm">
                        <div class="form-group">
                            <label for="editTaskName">Task Name *</label>
                            <input type="text" id="editTaskName" name="name" value="${task.name}" required class="form-input">
                        </div>
                        <div class="form-group">
                            <label for="editTaskDescription">Description</label>
                            <textarea id="editTaskDescription" name="description" class="form-input" rows="3">${task.description || ''}</textarea>
                        </div>
                        <div class="form-group">
                            <label for="editTaskType">Task Type *</label>
                            <select id="editTaskType" name="type" required class="form-select">
                                <option value="FIXED_RATE" ${task.type === 'FIXED_RATE' ? 'selected' : ''}>Fixed Rate</option>
                                <option value="FIXED_DELAY" ${task.type === 'FIXED_DELAY' ? 'selected' : ''}>Fixed Delay</option>
                                <option value="CRON" ${task.type === 'CRON' ? 'selected' : ''}>Cron Expression</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="editTaskSchedule">Schedule *</label>
                            <input type="text" id="editTaskSchedule" name="schedule" value="${task.schedule}" required class="form-input">
                        </div>
                        <div class="form-group">
                            <label>
                                <input type="checkbox" id="editTaskActive" name="active" ${task.active ? 'checked' : ''}>
                                Active
                            </label>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" onclick="closeModal()">Cancel</button>
                    <button class="btn btn-primary" onclick="updateTask('${taskId}')">Update Task</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
    } catch (error) {
        console.error('Error loading task for editing:', error);
        showNotification('Error loading task', 'error');
    }
}

// Update task
async function updateTask(taskId) {
    const form = document.getElementById('editTaskForm');
    const formData = new FormData(form);
    
    const taskData = {
        name: formData.get('name'),
        description: formData.get('description'),
        type: formData.get('type'),
        schedule: formData.get('schedule'),
        active: formData.get('active') === 'on'
    };
    
    try {
        const response = await fetch(`/api/tasks/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(taskData)
        });
        
        if (response.ok) {
            closeModal();
            showNotification('Task updated successfully!', 'success');
            loadTasks();
        } else {
            showNotification('Error updating task', 'error');
        }
    } catch (error) {
        console.error('Error updating task:', error);
        showNotification('Error updating task', 'error');
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
            showNotification('Task deleted successfully!', 'success');
            loadTasks();
        } else {
            showNotification('Error deleting task', 'error');
        }
    } catch (error) {
        console.error('Error deleting task:', error);
        showNotification('Error deleting task', 'error');
    }
}

// Pause task
async function pauseTask(taskId) {
    try {
        const response = await fetch(`/api/tasks/${taskId}/pause`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showNotification('Task paused successfully!', 'success');
            loadTasks();
        } else {
            showNotification('Error pausing task', 'error');
        }
    } catch (error) {
        console.error('Error pausing task:', error);
        showNotification('Error pausing task', 'error');
    }
}

// Resume task
async function resumeTask(taskId) {
    try {
        const response = await fetch(`/api/tasks/${taskId}/resume`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showNotification('Task resumed successfully!', 'success');
            loadTasks();
        } else {
            showNotification('Error resuming task', 'error');
        }
    } catch (error) {
        console.error('Error resuming task:', error);
        showNotification('Error resuming task', 'error');
    }
}

// Execute task
async function executeTask(taskId) {
    try {
        const response = await fetch(`/api/tasks/${taskId}/execute`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showNotification('Task executed successfully!', 'success');
            loadTasks();
        } else {
            showNotification('Error executing task', 'error');
        }
    } catch (error) {
        console.error('Error executing task:', error);
        showNotification('Error executing task', 'error');
    }
}

// Refresh tasks
function refreshTasks() {
    loadTasks();
    showNotification('Tasks refreshed!', 'info');
}

// Close modal
function closeModal() {
    const modal = document.querySelector('.modal-overlay');
    if (modal) {
        modal.remove();
    }
}

// Show notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
            <span>${message}</span>
        </div>
        <button class="notification-close" onclick="this.parentElement.remove()">&times;</button>
    `;
    
    document.body.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}

// Format date time
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return 'N/A';
    
    const date = new Date(dateTimeString);
    return date.toLocaleString();
}

// Show schedules view
function showSchedulesView() {
    const dashboardContent = document.querySelector('.dashboard-content');
    if (!dashboardContent) return;
    
    dashboardContent.innerHTML = `
        <div class="section-header">
            <h3>Schedule Configuration</h3>
            <div class="section-actions">
                <button class="btn btn-primary">
                    <i class="fas fa-plus"></i>
                    New Schedule
                </button>
                <button class="btn btn-outline">
                    <i class="fas fa-calendar-plus"></i>
                    Bulk Import
                </button>
            </div>
        </div>
        
        <div class="schedule-calendar">
            <div class="calendar-header">
                <h4>Upcoming Scheduled Tasks</h4>
            </div>
            <div class="calendar-grid">
                <div class="calendar-day">
                    <div class="day-header">Today</div>
                    <div class="scheduled-task">
                        <span class="time">15:07:00</span>
                        <span class="task-name">System Health Check</span>
                    </div>
                    <div class="scheduled-task">
                        <span class="time">15:07:15</span>
                        <span class="task-name">System Cleanup</span>
                    </div>
                    <div class="scheduled-task">
                        <span class="time">15:08:00</span>
                        <span class="task-name">Daily Report Generation</span>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Show executions view
function showExecutionsView() {
    const dashboardContent = document.querySelector('.dashboard-content');
    if (!dashboardContent) return;
    
    dashboardContent.innerHTML = `
        <div class="section-header">
            <h3>Execution History</h3>
            <div class="section-actions">
                <button class="btn btn-outline">
                    <i class="fas fa-filter"></i>
                    Filter
                </button>
                <button class="btn btn-outline">
                    <i class="fas fa-download"></i>
                    Export
                </button>
            </div>
        </div>
        
        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Task Name</th>
                        <th>Type</th>
                        <th>Status</th>
                        <th>Start Time</th>
                        <th>Duration</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="success-row">
                        <td>System Health Check</td>
                        <td><span class="badge badge-secondary">FIXED_RATE</span></td>
                        <td><span class="status-badge success">SUCCESS</span></td>
                        <td>15:06:37</td>
                        <td>2980ms</td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-icon" title="View Details">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <button class="btn btn-sm btn-icon" title="Rerun">
                                    <i class="fas fa-redo"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <tr class="success-row">
                        <td>System Cleanup</td>
                        <td><span class="badge badge-secondary">FIXED_DELAY</span></td>
                        <td><span class="status-badge success">SUCCESS</span></td>
                        <td>15:06:26</td>
                        <td>4384ms</td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-icon" title="View Details">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <button class="btn btn-sm btn-icon" title="Rerun">
                                    <i class="fas fa-redo"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    `;
}

// Show monitoring view
function showMonitoringView() {
    const dashboardContent = document.querySelector('.dashboard-content');
    if (!dashboardContent) return;
    
    dashboardContent.innerHTML = `
        <div class="section-header">
            <h3>System Monitoring</h3>
            <div class="section-actions">
                <button class="btn btn-outline">
                    <i class="fas fa-cog"></i>
                    Alerts
                </button>
                <button class="btn btn-outline">
                    <i class="fas fa-download"></i>
                    Report
                </button>
            </div>
        </div>
        
        <div class="monitoring-grid">
            <div class="monitoring-card">
                <h4>System Performance</h4>
                <div class="performance-metrics">
                    <div class="metric">
                        <span class="label">CPU Usage</span>
                        <span class="value">23%</span>
                    </div>
                    <div class="metric">
                        <span class="label">Memory Usage</span>
                        <span class="value">45%</span>
                    </div>
                    <div class="metric">
                        <span class="label">Disk Usage</span>
                        <span class="value">67%</span>
                    </div>
                </div>
            </div>
            
            <div class="monitoring-card">
                <h4>Application Health</h4>
                <div class="health-indicators">
                    <div class="indicator healthy">
                        <span class="dot"></span>
                        <span>Database Connection</span>
                    </div>
                    <div class="indicator healthy">
                        <span class="dot"></span>
                        <span>Task Scheduler</span>
                    </div>
                    <div class="indicator healthy">
                        <span class="dot"></span>
                        <span>Web Server</span>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Show settings view
function showSettingsView() {
    const dashboardContent = document.querySelector('.dashboard-content');
    if (!dashboardContent) return;
    
    dashboardContent.innerHTML = `
        <div class="section-header">
            <h3>Application Settings</h3>
            <div class="section-actions">
                <button class="btn btn-primary">Save Changes</button>
                <button class="btn btn-outline">Reset to Default</button>
            </div>
        </div>
        
        <div class="settings-grid">
            <div class="settings-card">
                <h4>General Settings</h4>
                <div class="setting-item">
                    <label>Application Name</label>
                    <input type="text" value="TaskScheduler Pro" class="form-input">
                </div>
                <div class="setting-item">
                    <label>Auto-refresh Interval</label>
                    <select class="form-select">
                        <option value="5">5 seconds</option>
                        <option value="10" selected>10 seconds</option>
                        <option value="30">30 seconds</option>
                    </select>
                </div>
            </div>
            
            <div class="settings-card">
                <h4>Notification Settings</h4>
                <div class="setting-item">
                    <label>
                        <input type="checkbox" checked> Email Notifications
                    </label>
                </div>
                <div class="setting-item">
                    <label>
                        <input type="checkbox" checked> Task Failure Alerts
                    </label>
                </div>
            </div>
        </div>
    `;
}

// Enhanced chart initialization
function initializeCharts() {
    if (typeof Chart === 'undefined') return;
    
    // Task Execution Overview Chart
    initializeExecutionChart();
    
    // Task Status Distribution Chart
    initializeStatusChart();
}

function initializeExecutionChart() {
    const ctx = document.getElementById('executionChart');
    if (!ctx) return;
    
    // Get data from the page
    const healthCount = parseInt(document.querySelector('.metric-card:nth-child(1) .metric-value')?.textContent || '0');
    const cleanupCount = parseInt(document.querySelector('.metric-card:nth-child(2) .metric-value')?.textContent || '0');
    const reportsCount = parseInt(document.querySelector('.metric-card:nth-child(3) .metric-value')?.textContent || '0');
    
    // Generate time labels for the last 7 days
    const timeLabels = generateTimeLabels(7);
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: timeLabels,
            datasets: [
                {
                    label: 'Health Checks',
                    data: generateRandomData(7, healthCount),
                    borderColor: '#10b981',
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Cleanup Tasks',
                    data: generateRandomData(7, cleanupCount),
                    borderColor: '#f59e0b',
                    backgroundColor: 'rgba(245, 158, 11, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Reports Generated',
                    data: generateRandomData(7, reportsCount),
                    borderColor: '#3b82f6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index'
            },
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 20,
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: '#fff',
                    bodyColor: '#fff',
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    borderWidth: 1,
                    cornerRadius: 8,
                    displayColors: true
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        color: '#64748b',
                        font: {
                            size: 11
                        }
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        color: '#64748b',
                        font: {
                            size: 11
                        }
                    }
                }
            }
        }
    });
}

function initializeStatusChart() {
    const ctx = document.getElementById('statusChart');
    if (!ctx) return;
    
    // Count success and error rows
    const successRows = document.querySelectorAll('.success-row').length;
    const errorRows = document.querySelectorAll('.error-row').length;
    const total = successRows + errorRows;
    
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Success', 'Error', 'Pending'],
            datasets: [{
                data: [successRows, errorRows, Math.max(0, total - successRows - errorRows)],
                backgroundColor: [
                    '#10b981',
                    '#ef4444',
                    '#f59e0b'
                ],
                borderWidth: 0,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        padding: 20,
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: '#fff',
                    bodyColor: '#fff',
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    borderWidth: 1,
                    cornerRadius: 8
                }
            },
            cutout: '60%'
        }
    });
}

// Interactive features
function initializeInteractiveFeatures() {
    // Metric card interactions
    const metricCards = document.querySelectorAll('.metric-card');
    metricCards.forEach(card => {
        card.addEventListener('click', function() {
            // Add click animation
            this.style.transform = 'scale(0.98)';
            setTimeout(() => {
                this.style.transform = 'translateY(-2px)';
            }, 150);
        });
        
        // Add hover effects
        card.addEventListener('mouseenter', function() {
            this.style.boxShadow = '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.boxShadow = '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1)';
        });
    });
    
    // Table row interactions
    const tableRows = document.querySelectorAll('.data-table tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('click', function() {
            // Remove active class from all rows
            tableRows.forEach(r => r.classList.remove('active'));
            // Add active class to clicked row
            this.classList.add('active');
        });
    });
    
    // Button interactions
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            // Add ripple effect
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.width = ripple.style.height = size + 'px';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            ripple.classList.add('ripple');
            
            this.appendChild(ripple);
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
}

// Real-time updates simulation
function initializeRealTimeUpdates() {
    // Simulate real-time metric updates
    setInterval(() => {
        updateMetrics();
    }, 30000); // Update every 30 seconds
    
    // Add loading states
    addLoadingStates();
}

function updateMetrics() {
    const metricValues = document.querySelectorAll('.metric-value');
    metricValues.forEach(value => {
        const currentValue = parseInt(value.textContent);
        const newValue = currentValue + Math.floor(Math.random() * 5) + 1;
        value.textContent = newValue;
        
        // Add update animation
        value.style.transform = 'scale(1.1)';
        value.style.color = '#10b981';
        setTimeout(() => {
            value.style.transform = 'scale(1)';
            value.style.color = '';
        }, 300);
    });
}

function addLoadingStates() {
    // Add loading skeleton for initial load
    const loadingElements = document.querySelectorAll('.metric-card, .chart-card');
    loadingElements.forEach(element => {
        element.style.opacity = '0.7';
        setTimeout(() => {
            element.style.opacity = '1';
        }, 1000);
    });
}

// Professional features
function addProfessionalFeatures() {
    // Add keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl/Cmd + K for search
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
            e.preventDefault();
            showSearchModal();
        }
        
        // Escape to close modals/sidebar
        if (e.key === 'Escape') {
            const sidebar = document.querySelector('.sidebar');
            if (sidebar && sidebar.classList.contains('open')) {
                sidebar.classList.remove('open');
            }
        }
    });
    
    // Add smooth scrolling
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    // Add tooltips
    addTooltips();
}

function showSearchModal() {
    // Create a simple search modal
    const modal = document.createElement('div');
    modal.className = 'search-modal';
    modal.innerHTML = `
        <div class="search-overlay">
            <div class="search-container">
                <input type="text" placeholder="Search tasks, schedules, executions..." class="search-input">
                <button class="search-close">&times;</button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    const input = modal.querySelector('.search-input');
    const closeBtn = modal.querySelector('.search-close');
    
    input.focus();
    
    closeBtn.addEventListener('click', () => modal.remove());
    modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.remove();
    });
}

function addTooltips() {
    const tooltipElements = document.querySelectorAll('[title]');
    tooltipElements.forEach(element => {
        element.addEventListener('mouseenter', function(e) {
            const tooltip = document.createElement('div');
            tooltip.className = 'tooltip';
            tooltip.textContent = this.getAttribute('title');
            document.body.appendChild(tooltip);
            
            const rect = this.getBoundingClientRect();
            tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
            tooltip.style.top = rect.top - tooltip.offsetHeight - 8 + 'px';
        });
        
        element.addEventListener('mouseleave', function() {
            const tooltip = document.querySelector('.tooltip');
            if (tooltip) tooltip.remove();
        });
    });
}

// Utility functions
function generateTimeLabels(days) {
    const labels = [];
    const today = new Date();
    
    for (let i = days - 1; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(date.getDate() - i);
        labels.push(date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
    }
    
    return labels;
}

function generateRandomData(days, baseValue) {
    const data = [];
    for (let i = 0; i < days; i++) {
        data.push(Math.max(0, baseValue + Math.floor(Math.random() * 20) - 10));
    }
    return data;
}

// Add CSS for additional features
const additionalStyles = `
    .search-modal {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        z-index: 2000;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .search-overlay {
        background: white;
        border-radius: 12px;
        padding: 20px;
        width: 90%;
        max-width: 500px;
        box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
    }
    
    .search-container {
        display: flex;
        align-items: center;
        gap: 12px;
    }
    
    .search-input {
        flex: 1;
        padding: 12px 16px;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        font-size: 16px;
        outline: none;
    }
    
    .search-input:focus {
        border-color: #2563eb;
        box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
    }
    
    .search-close {
        background: none;
        border: none;
        font-size: 24px;
        cursor: pointer;
        color: #64748b;
        padding: 4px;
    }
    
    .tooltip {
        position: absolute;
        background: #1e293b;
        color: white;
        padding: 8px 12px;
        border-radius: 6px;
        font-size: 12px;
        z-index: 1000;
        pointer-events: none;
        white-space: nowrap;
    }
    
    .tooltip::after {
        content: '';
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        border: 4px solid transparent;
        border-top-color: #1e293b;
    }
    
    .ripple {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.3);
        transform: scale(0);
        animation: ripple-animation 0.6s linear;
        pointer-events: none;
    }
    
    @keyframes ripple-animation {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
    
    .data-table tbody tr.active {
        background: #eff6ff !important;
        border-left: 4px solid #2563eb;
    }
    
    .btn {
        position: relative;
        overflow: hidden;
    }
`;

// Inject additional styles
const styleSheet = document.createElement('style');
styleSheet.textContent = additionalStyles;
document.head.appendChild(styleSheet);

// Console logging for debugging
console.log('🔧 Professional dashboard features loaded');
console.log('⌨️  Keyboard shortcuts: Ctrl/Cmd + K for search, Esc to close');
