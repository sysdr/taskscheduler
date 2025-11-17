const API_URL = 'http://localhost:8080/api/v1';
const DEMO_CONFIG_URL = 'demo-config.json';
let currentToken = null;
let demoHydrated = false;

document.addEventListener('DOMContentLoaded', () => {
    loadDemoConfig();
});

async function loadDemoConfig() {
    try {
        const response = await fetch(DEMO_CONFIG_URL, { cache: 'no-store' });
        if (!response.ok) {
            return;
        }

        const config = await response.json();
        if (!config.apiKey) {
            return;
        }

        document.getElementById('apiKey').value = config.apiKey;
        if (config.tenantName) {
            document.getElementById('tenantInfo').innerHTML = `
                <h3>${config.tenantName} Demo Tenant</h3>
                <p><strong>API Key:</strong> ${config.apiKey}</p>
                ${config.lastUpdated ? `<p class="subtitle">Demo data refreshed ${new Date(config.lastUpdated).toLocaleString()}</p>` : ''}
            `;
        }

        await hydrateDemoData(config.apiKey);
    } catch (error) {
        console.warn('Demo configuration could not be loaded.', error);
    }
}

async function hydrateDemoData(apiKey) {
    if (demoHydrated) {
        return;
    }

    try {
        const token = await getToken(apiKey);
        currentToken = token;
        await Promise.all([loadTasks(), loadMetrics()]);
        demoHydrated = true;
    } catch (error) {
        console.warn('Failed to hydrate demo dashboard data.', error);
    }
}

async function registerTenant() {
    const tenantName = document.getElementById('tenantName').value;
    if (!tenantName) {
        alert('Please enter tenant name');
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ tenantName })
        });
        
        const data = await response.json();
        
        document.getElementById('tenantInfo').innerHTML = `
            <h3>Tenant Registered Successfully!</h3>
            <p><strong>Tenant ID:</strong> ${data.tenantId}</p>
            <p><strong>API Key:</strong> ${data.apiKey}</p>
            <p style="color: #f56565; margin-top: 10px;">Save this API key - it won't be shown again!</p>
        `;
        
        document.getElementById('apiKey').value = data.apiKey;
    } catch (error) {
        alert('Registration failed: ' + error.message);
    }
}

async function getToken(apiKey) {
    try {
        const response = await fetch(`${API_URL}/auth/token`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ apiKey })
        });
        
        const data = await response.json();
        return data.token;
    } catch (error) {
        throw new Error('Failed to get token');
    }
}

async function submitTask() {
    const apiKey = document.getElementById('apiKey').value;
    const taskName = document.getElementById('taskName').value;
    const payload = document.getElementById('taskPayload').value;
    const priority = parseInt(document.getElementById('priority').value);
    
    if (!apiKey || !taskName) {
        alert('Please fill in all required fields');
        return;
    }
    
    try {
        const token = await getToken(apiKey);
        currentToken = token;
        
        const response = await fetch(`${API_URL}/tasks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                taskName,
                payload,
                priority,
                maxRetries: 3
            })
        });
        
        const task = await response.json();
        alert(`Task submitted successfully! Task ID: ${task.taskId}`);
        
        loadTasks();
        loadMetrics();
    } catch (error) {
        alert('Failed to submit task: ' + error.message);
    }
}

async function loadTasks() {
    const apiKey = document.getElementById('apiKey').value;
    if (!apiKey) return;
    
    try {
        const token = currentToken || await getToken(apiKey);
        currentToken = token;
        
        const statusFilter = document.getElementById('statusFilter').value;
        let url = `${API_URL}/tasks`;
        if (statusFilter) {
            url += `?status=${statusFilter}`;
        }
        
        const response = await fetch(url, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        const tasks = await response.json();
        
        const tasksList = document.getElementById('tasksList');
        tasksList.innerHTML = tasks.map(task => `
            <div class="task-item">
                <div class="task-header">
                    <span class="task-name">${task.taskName}</span>
                    <span class="task-status status-${task.status}">${task.status}</span>
                </div>
                <div class="task-details">
                    <p><strong>Task ID:</strong> ${task.taskId}</p>
                    <p><strong>Priority:</strong> ${task.priority}</p>
                    <p><strong>Submitted:</strong> ${new Date(task.submittedAt).toLocaleString()}</p>
                    ${task.completedAt ? `<p><strong>Completed:</strong> ${new Date(task.completedAt).toLocaleString()}</p>` : ''}
                    ${task.result ? `<p><strong>Result:</strong> ${task.result}</p>` : ''}
                    ${task.errorMessage ? `<p style="color: #f56565;"><strong>Error:</strong> ${task.errorMessage}</p>` : ''}
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Failed to load tasks:', error);
    }
}

async function loadMetrics() {
    const apiKey = document.getElementById('apiKey').value;
    if (!apiKey) return;
    
    try {
        const token = currentToken || await getToken(apiKey);
        currentToken = token;
        
        const response = await fetch(`${API_URL}/metrics`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        const metrics = await response.json();
        
        document.getElementById('pendingCount').textContent = metrics.pending;
        document.getElementById('scheduledCount').textContent = metrics.scheduled;
        document.getElementById('runningCount').textContent = metrics.running;
        document.getElementById('completedCount').textContent = metrics.completed;
        document.getElementById('failedCount').textContent = metrics.failed;
        document.getElementById('totalCount').textContent = metrics.total;
    } catch (error) {
        console.error('Failed to load metrics:', error);
    }
}

// Auto-refresh every 5 seconds
setInterval(() => {
    if (currentToken) {
        loadTasks();
        loadMetrics();
    }
}, 5000);
