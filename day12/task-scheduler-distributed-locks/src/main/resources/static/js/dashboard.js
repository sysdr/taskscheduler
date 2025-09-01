// Dashboard JavaScript
let refreshInterval;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    refreshStats();
    refreshRunningTasks();
    
    // Auto-refresh every 5 seconds
    refreshInterval = setInterval(() => {
        refreshStats();
        refreshRunningTasks();
    }, 5000);
    
    // Setup form submission
    document.getElementById('taskForm').addEventListener('submit', function(e) {
        e.preventDefault();
        executeTask();
    });
});

// Refresh statistics
async function refreshStats() {
    try {
        const response = await fetch('/api/locks/statistics');
        const stats = await response.json();
        
        document.getElementById('activeLocks').textContent = stats.totalActiveLocks;
        document.getElementById('totalAcquisitions').textContent = stats.totalAcquisitions;
        document.getElementById('totalReleases').textContent = stats.totalReleases;
        document.getElementById('totalTimeouts').textContent = stats.totalTimeouts;
    } catch (error) {
        console.error('Error refreshing stats:', error);
    }
}

// Execute task
async function executeTask() {
    const taskKey = document.getElementById('taskKey').value;
    const taskType = document.getElementById('taskType').value;
    const resultDiv = document.getElementById('taskResult');
    const form = document.getElementById('taskForm');
    
    if (!taskKey.trim()) {
        showMessage(resultDiv, 'Task key is required', 'error');
        return;
    }
    
    form.classList.add('loading');
    
    try {
        const response = await fetch('/api/tasks/execute', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                taskKey: taskKey,
                taskType: taskType
            })
        });
        
        if (response.ok) {
            const result = await response.json();
            showTaskResult(resultDiv, result);
            
            // Clear form
            document.getElementById('taskKey').value = '';
            
            // Refresh running tasks
            setTimeout(refreshRunningTasks, 1000);
        } else {
            const error = await response.text();
            showMessage(resultDiv, `Error: ${error}`, 'error');
        }
    } catch (error) {
        showMessage(resultDiv, `Network error: ${error.message}`, 'error');
    } finally {
        form.classList.remove('loading');
    }
}

// Refresh running tasks
async function refreshRunningTasks() {
    try {
        const response = await fetch('/api/tasks/running');
        const tasks = await response.json();
        
        const container = document.getElementById('runningTasks');
        
        if (tasks.length === 0) {
            container.innerHTML = '<p class="text-gray-500">No running tasks</p>';
        } else {
            container.innerHTML = tasks.map(task => `
                <div class="task-running">
                    <div class="flex justify-between items-center">
                        <div>
                            <span class="font-medium">${task.taskKey}</span>
                            <span class="text-sm text-gray-600 ml-2">(${task.instanceId})</span>
                        </div>
                        <span class="text-xs text-gray-500">${formatDuration(task.startedAt)}</span>
                    </div>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('Error refreshing running tasks:', error);
    }
}

// Run concurrent execution test
async function runConcurrentTest() {
    const testTaskKey = document.getElementById('testTaskKey').value;
    const concurrentCount = parseInt(document.getElementById('concurrentCount').value);
    const resultsDiv = document.getElementById('concurrentResults');
    
    if (!testTaskKey.trim()) {
        showMessage(resultsDiv, 'Test task key is required', 'error');
        return;
    }
    
    showMessage(resultsDiv, `Starting ${concurrentCount} concurrent executions...`, 'info');
    
    const promises = [];
    for (let i = 0; i < concurrentCount; i++) {
        promises.push(
            fetch('/api/tasks/execute', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    taskKey: testTaskKey,
                    taskType: 'data-processing'
                })
            }).then(response => response.json())
        );
    }
    
    try {
        const results = await Promise.all(promises);
        
        const successCount = results.filter(r => r.status === 'COMPLETED').length;
        const failCount = results.filter(r => r.status === 'FAILED').length;
        
        showMessage(resultsDiv, 
            `Test completed: ${successCount} succeeded, ${failCount} failed. ` +
            `Only one should have succeeded due to distributed locking!`, 
            successCount === 1 ? 'success' : 'error'
        );
        
        // Refresh stats and running tasks
        setTimeout(() => {
            refreshStats();
            refreshRunningTasks();
        }, 1000);
        
    } catch (error) {
        showMessage(resultsDiv, `Test failed: ${error.message}`, 'error');
    }
}

// Check lock status
async function checkLockStatus() {
    const lockKey = document.getElementById('lockKeyInput').value;
    const infoDiv = document.getElementById('lockInfo');
    
    if (!lockKey.trim()) {
        showMessage(infoDiv, 'Lock key is required', 'error');
        return;
    }
    
    try {
        const [infoResponse, heldResponse] = await Promise.all([
            fetch(`/api/locks/info/${encodeURIComponent(lockKey)}`),
            fetch(`/api/locks/is-held/${encodeURIComponent(lockKey)}`)
        ]);
        
        const info = await infoResponse.json();
        const held = await heldResponse.json();
        
        if (info.ownerInstance) {
            infoDiv.innerHTML = `
                <div class="bg-blue-50 border border-blue-200 rounded p-4">
                    <h4 class="font-medium text-blue-800">Lock Information</h4>
                    <div class="mt-2 text-sm">
                        <p><strong>Key:</strong> ${info.lockKey}</p>
                        <p><strong>Owner:</strong> ${info.ownerInstance}</p>
                        <p><strong>Acquired:</strong> ${formatDateTime(info.acquiredAt)}</p>
                        <p><strong>Expires:</strong> ${formatDateTime(info.expiresAt)}</p>
                        <p><strong>Status:</strong> ${info.isExpired ? 'Expired' : 'Active'}</p>
                        <p><strong>Currently Held:</strong> ${held.isHeld ? 'Yes' : 'No'}</p>
                    </div>
                </div>
            `;
        } else {
            showMessage(infoDiv, 'Lock not found or never acquired', 'info');
        }
    } catch (error) {
        showMessage(infoDiv, `Error checking lock: ${error.message}`, 'error');
    }
}

// Cleanup expired locks
async function cleanupLocks() {
    try {
        const response = await fetch('/api/locks/cleanup', { method: 'POST' });
        const result = await response.json();
        
        const infoDiv = document.getElementById('lockInfo');
        showMessage(infoDiv, `Cleaned up ${result.cleanedLocks} expired locks`, 'success');
        
        // Refresh stats
        setTimeout(refreshStats, 1000);
    } catch (error) {
        console.error('Error cleaning up locks:', error);
    }
}

// Utility functions
function showMessage(container, message, type) {
    container.innerHTML = `<div class="${type}-message">${message}</div>`;
    container.classList.remove('hidden');
}

function showTaskResult(container, result) {
    const statusClass = result.status === 'COMPLETED' ? 'success' : 
                       result.status === 'FAILED' ? 'error' : 'info';
    
    container.innerHTML = `
        <div class="${statusClass}-message">
            <h4 class="font-medium">Task ${result.status}</h4>
            <p class="text-sm mt-1">
                <strong>Key:</strong> ${result.taskKey}<br>
                <strong>Duration:</strong> ${result.durationMs || 0}ms<br>
                ${result.result ? `<strong>Result:</strong> ${result.result}` : ''}
                ${result.errorMessage ? `<strong>Error:</strong> ${result.errorMessage}` : ''}
            </p>
        </div>
    `;
    container.classList.remove('hidden');
}

function formatDuration(startTime) {
    if (!startTime) return 'Unknown';
    
    const start = new Date(startTime);
    const now = new Date();
    const diffMs = now - start;
    
    if (diffMs < 1000) return `${diffMs}ms`;
    if (diffMs < 60000) return `${Math.floor(diffMs / 1000)}s`;
    return `${Math.floor(diffMs / 60000)}m ${Math.floor((diffMs % 60000) / 1000)}s`;
}

function formatDateTime(dateTime) {
    if (!dateTime) return 'N/A';
    return new Date(dateTime).toLocaleString();
}
