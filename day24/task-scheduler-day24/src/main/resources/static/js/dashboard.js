let tasksData = [];

document.addEventListener('DOMContentLoaded', function() {
    // Load initial tasks
    refreshTasks();
    
    // Set up form submission
    document.getElementById('createTaskForm').addEventListener('submit', handleCreateTask);
    
    // Auto-refresh every 10 seconds
    setInterval(refreshTasks, 10000);
});

function handleCreateTask(event) {
    event.preventDefault();
    
    const formData = new FormData();
    formData.append('name', document.getElementById('taskName').value);
    formData.append('taskType', document.getElementById('taskType').value);
    formData.append('description', document.getElementById('taskDescription').value);
    formData.append('priority', document.getElementById('taskPriority').value);
    formData.append('maxRetries', document.getElementById('maxRetries').value);
    formData.append('taskData', document.getElementById('taskData').value);
    
    fetch('/tasks', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(task => {
        console.log('Task created:', task);
        // Reset form
        document.getElementById('createTaskForm').reset();
        document.getElementById('maxRetries').value = '3';
        document.getElementById('taskPriority').value = 'NORMAL';
        
        // Refresh tasks list
        refreshTasks();
        
        // Show success message
        showAlert('Task created successfully!', 'success');
    })
    .catch(error => {
        console.error('Error creating task:', error);
        showAlert('Error creating task!', 'danger');
    });
}

function refreshTasks() {
    fetch('/tasks/status/PENDING')
        .then(response => response.json())
        .then(pendingTasks => {
            return fetch('/tasks/status/RUNNING')
                .then(response => response.json())
                .then(runningTasks => {
                    return fetch('/tasks/status/RETRYING')
                        .then(response => response.json())
                        .then(retryingTasks => {
                            return fetch('/tasks/status/COMPLETED')
                                .then(response => response.json())
                                .then(completedTasks => {
                                    return fetch('/tasks/status/FAILED')
                                        .then(response => response.json())
                                        .then(failedTasks => {
                                            tasksData = [
                                                ...pendingTasks,
                                                ...runningTasks, 
                                                ...retryingTasks,
                                                ...completedTasks.slice(-10), // Last 10 completed
                                                ...failedTasks
                                            ];
                                            updateTasksTable();
                                        });
                                });
                        });
                });
        })
        .catch(error => {
            console.error('Error fetching tasks:', error);
        });
}

function updateTasksTable() {
    const tbody = document.querySelector('#tasksTable tbody');
    tbody.innerHTML = '';
    
    // Sort tasks by ID descending (newest first)
    const sortedTasks = tasksData.sort((a, b) => b.id - a.id);
    
    sortedTasks.forEach(task => {
        const row = document.createElement('tr');
        
        const statusClass = getStatusClass(task.status);
        const nextRetryText = task.nextRetryTime ? 
            new Date(task.nextRetryTime).toLocaleString() : 'N/A';
        
        row.innerHTML = `
            <td>${task.id}</td>
            <td>${task.name}</td>
            <td>${task.taskType}</td>
            <td><span class="badge ${statusClass}">${task.status}</span></td>
            <td>${task.priority}</td>
            <td>${task.attemptCount}/${task.maxRetries}</td>
            <td>${new Date(task.createdTime).toLocaleString()}</td>
            <td>${nextRetryText}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="viewTaskDetails(${task.id})" title="View Details">
                    <i class="fas fa-eye"></i>
                </button>
                ${task.status === 'PENDING' || task.status === 'RETRYING' ? 
                    `<button class="btn btn-sm btn-outline-danger ms-1" onclick="cancelTask(${task.id})" title="Cancel">
                        <i class="fas fa-times"></i>
                    </button>` : ''}
            </td>
        `;
        
        tbody.appendChild(row);
    });
}

function getStatusClass(status) {
    switch(status) {
        case 'COMPLETED': return 'bg-success';
        case 'FAILED': return 'bg-danger';
        case 'RUNNING': return 'bg-primary';
        case 'RETRYING': return 'bg-warning text-dark';
        case 'CANCELLED': return 'bg-secondary';
        default: return 'bg-info';
    }
}

function viewTaskDetails(taskId) {
    const task = tasksData.find(t => t.id === taskId);
    if (task) {
        const details = `
Task ID: ${task.id}
Name: ${task.name}
Type: ${task.taskType}
Status: ${task.status}
Priority: ${task.priority}
Attempts: ${task.attemptCount}/${task.maxRetries}
Created: ${new Date(task.createdTime).toLocaleString()}
${task.startedTime ? 'Started: ' + new Date(task.startedTime).toLocaleString() + '\n' : ''}
${task.completedTime ? 'Completed: ' + new Date(task.completedTime).toLocaleString() + '\n' : ''}
${task.lastAttemptTime ? 'Last Attempt: ' + new Date(task.lastAttemptTime).toLocaleString() + '\n' : ''}
${task.nextRetryTime ? 'Next Retry: ' + new Date(task.nextRetryTime).toLocaleString() + '\n' : ''}
${task.errorMessage ? 'Error: ' + task.errorMessage + '\n' : ''}
${task.taskData ? 'Data: ' + task.taskData : ''}
        `;
        alert(details);
    }
}

function cancelTask(taskId) {
    if (confirm('Are you sure you want to cancel this task?')) {
        fetch(`/tasks/${taskId}/status?status=CANCELLED`, {
            method: 'PUT'
        })
        .then(response => {
            if (response.ok) {
                showAlert('Task cancelled successfully!', 'success');
                refreshTasks();
            } else {
                showAlert('Error cancelling task!', 'danger');
            }
        })
        .catch(error => {
            console.error('Error cancelling task:', error);
            showAlert('Error cancelling task!', 'danger');
        });
    }
}

function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.querySelector('.container-fluid').insertBefore(alertDiv, document.querySelector('.row'));
    
    // Auto-dismiss after 3 seconds
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}
