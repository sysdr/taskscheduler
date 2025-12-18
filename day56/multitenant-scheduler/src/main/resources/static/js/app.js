let currentTenantId = null;
let currentApiKey = null;

// Load initial data
window.onload = function() {
    loadTenants();
    refreshDashboard(); // Load dashboard immediately
    setInterval(refreshDashboard, 5000); // Refresh every 5 seconds
};

async function createTenant() {
    const name = document.getElementById('tenantName').value;
    const maxConcurrent = document.getElementById('maxConcurrent').value;
    const maxDaily = document.getElementById('maxDaily').value;
    
    if (!name) {
        alert('Please enter tenant name');
        return;
    }
    
    const response = await fetch('/api/tenants', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            name: name,
            maxConcurrentTasks: parseInt(maxConcurrent),
            maxTasksPerDay: parseInt(maxDaily)
        })
    });
    
    if (response.ok) {
        document.getElementById('tenantName').value = '';
        loadTenants();
        refreshDashboard();
    }
}

async function loadTenants() {
    const response = await fetch('/api/tenants');
    const tenants = await response.json();
    
    const container = document.getElementById('tenantsContainer');
    const select = document.getElementById('tenantSelect');
    
    container.innerHTML = tenants.map(t => `
        <div class="tenant-card" onclick="selectTenant('${t.tenantId}', '${t.apiKey}')">
            <h3>${t.name}</h3>
            <p>🆔 ${t.tenantId.substring(0, 8)}...</p>
            <p>⚡ Max Concurrent: ${t.maxConcurrentTasks}</p>
            <p>📊 Max Daily: ${t.maxTasksPerDay}</p>
            <p>✅ ${t.active ? 'Active' : 'Inactive'}</p>
        </div>
    `).join('');
    
    select.innerHTML = '<option value="">Select Tenant...</option>' +
        tenants.map(t => `<option value="${t.tenantId}" data-apikey="${t.apiKey}">${t.name}</option>`).join('');
    
    // Handle dropdown change
    select.onchange = function() {
        const selectedOption = select.options[select.selectedIndex];
        if (selectedOption.value) {
            const apiKey = selectedOption.getAttribute('data-apikey');
            selectTenant(selectedOption.value, apiKey);
        } else {
            currentTenantId = null;
            currentApiKey = null;
            document.getElementById('apiKey').value = '';
            document.getElementById('tasksContainer').innerHTML = '';
            loadMetrics(); // This will show the "Select a tenant" message
        }
    };
}

function selectTenant(tenantId, apiKey) {
    currentTenantId = tenantId;
    currentApiKey = apiKey;
    document.getElementById('tenantSelect').value = tenantId;
    document.getElementById('apiKey').value = apiKey;
    loadTasks();
    loadMetrics();
    refreshDashboard(); // Refresh dashboard when tenant is selected
}

async function createTask() {
    if (!currentApiKey) {
        alert('Please select a tenant first');
        return;
    }
    
    const taskName = document.getElementById('taskName').value;
    const taskDesc = document.getElementById('taskDesc').value;
    const cronExpr = document.getElementById('cronExpr').value;
    
    if (!taskName) {
        alert('Please enter task name');
        return;
    }
    
    const response = await fetch('/api/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-API-Key': currentApiKey
        },
        body: JSON.stringify({
            taskName: taskName,
            description: taskDesc,
            cronExpression: cronExpr
        })
    });
    
    if (response.ok) {
        document.getElementById('taskName').value = '';
        document.getElementById('taskDesc').value = '';
        loadTasks();
        loadMetrics();
        refreshDashboard();
    }
}

async function loadTasks() {
    if (!currentApiKey) return;
    
    const response = await fetch('/api/tasks', {
        headers: { 'X-API-Key': currentApiKey }
    });
    const tasks = await response.json();
    
    const container = document.getElementById('tasksContainer');
    container.innerHTML = tasks.map(t => `
        <div class="task-item">
            <h4>${t.taskName}</h4>
            <p>${t.description || 'No description'}</p>
            <p>📅 Cron: ${t.cronExpression}</p>
            <p>🔄 Executions: ${t.executionCount}</p>
            <p>⏰ Last Run: ${t.lastRunTime || 'Never'}</p>
            <p>⏭️ Next Run: ${t.nextRunTime || 'N/A'}</p>
            <span class="status status-${t.status.toLowerCase()}">${t.status}</span>
            ${t.lastResult ? `<p style="margin-top: 8px; font-size: 0.85rem;">📋 ${t.lastResult}</p>` : ''}
        </div>
    `).join('') || '<p>No tasks yet. Create your first task!</p>';
}

async function loadMetrics() {
    if (!currentApiKey) {
        const container = document.getElementById('metricsContainer');
        container.innerHTML = '<p style="text-align: center; color: #718096; padding: 20px;">Select a tenant to view metrics</p>';
        return;
    }
    
    try {
        const response = await fetch('/api/tasks/metrics', {
            headers: { 'X-API-Key': currentApiKey }
        });
        
        if (!response.ok) {
            throw new Error('Failed to fetch metrics');
        }
        
        const metrics = await response.json();
        
        const container = document.getElementById('metricsContainer');
        container.innerHTML = `
            <div class="metric-box">
                <h3>Running Tasks</h3>
                <div class="value">${metrics.currentRunningTasks || 0}</div>
            </div>
            <div class="metric-box">
                <h3>Tasks Today</h3>
                <div class="value">${metrics.tasksToday || 0}</div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading metrics:', error);
        const container = document.getElementById('metricsContainer');
        container.innerHTML = '<p style="text-align: center; color: #f56565; padding: 20px;">Error loading metrics</p>';
    }
}

async function refreshDashboard() {
    try {
        const response = await fetch('/api/tenants');
        if (!response.ok) {
            throw new Error('Failed to fetch tenants');
        }
        const tenants = await response.json();
        
        const dashboard = document.getElementById('dashboard');
        
        if (!tenants || tenants.length === 0) {
            dashboard.innerHTML = '<p style="text-align: center; color: #718096; padding: 20px;">No tenants created yet. Create a tenant to see resource usage.</p>';
            return;
        }
        
        const items = await Promise.all(tenants.map(async (tenant) => {
            try {
                const metricsResp = await fetch('/api/tasks/metrics', {
                    headers: { 'X-API-Key': tenant.apiKey }
                });
                
                if (!metricsResp.ok) {
                    throw new Error('Failed to fetch metrics');
                }
                
                const metrics = await metricsResp.json();
                
                const runningTasks = metrics.currentRunningTasks || 0;
                const tasksToday = metrics.tasksToday || 0;
                const usagePercent = Math.min(100, (runningTasks / tenant.maxConcurrentTasks) * 100);
                const dailyUsagePercent = Math.min(100, (tasksToday / tenant.maxTasksPerDay) * 100);
                
                return `
                    <div class="dashboard-item">
                        <h3>${tenant.name}</h3>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin: 16px 0;">
                            <div>
                                <div class="big-value" style="font-size: 2.5rem;">${runningTasks}</div>
                                <p style="font-size: 0.85rem; opacity: 0.9;">Running Tasks</p>
                                <div class="progress-bar" style="margin-top: 8px;">
                                    <div class="progress-fill" style="width: ${usagePercent}%"></div>
                                </div>
                                <p style="font-size: 0.75rem; margin-top: 4px;">${runningTasks} / ${tenant.maxConcurrentTasks} max</p>
                            </div>
                            <div>
                                <div class="big-value" style="font-size: 2.5rem;">${tasksToday}</div>
                                <p style="font-size: 0.85rem; opacity: 0.9;">Tasks Today</p>
                                <div class="progress-bar" style="margin-top: 8px;">
                                    <div class="progress-fill" style="width: ${dailyUsagePercent}%"></div>
                                </div>
                                <p style="font-size: 0.75rem; margin-top: 4px;">${tasksToday} / ${tenant.maxTasksPerDay} daily</p>
                            </div>
                        </div>
                    </div>
                `;
            } catch (error) {
                console.error(`Error loading metrics for tenant ${tenant.name}:`, error);
                return `
                    <div class="dashboard-item">
                        <h3>${tenant.name}</h3>
                        <p style="color: rgba(255,255,255,0.8);">Error loading metrics</p>
                    </div>
                `;
            }
        }));
        
        dashboard.innerHTML = items.join('');
    } catch (error) {
        console.error('Error refreshing dashboard:', error);
        const dashboard = document.getElementById('dashboard');
        dashboard.innerHTML = '<p style="text-align: center; color: #f56565; padding: 20px;">Error loading dashboard. Please refresh the page.</p>';
    }
}
