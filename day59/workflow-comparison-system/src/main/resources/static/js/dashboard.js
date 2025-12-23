let refreshInterval;

function init() {
    refreshDashboard();
    refreshInterval = setInterval(refreshDashboard, 2000);
}

async function refreshDashboard() {
    try {
        const [stats, orders] = await Promise.all([
            fetch('/api/statistics').then(r => r.json()),
            fetch('/api/orders').then(r => r.json())
        ]);

        updateStatistics(stats);
        updateOrders(orders);
        updateTemporalStatus(stats.temporalAvailable);
    } catch (error) {
        console.error('Error refreshing dashboard:', error);
    }
}

function updateStatistics(stats) {
    document.getElementById('totalOrders').textContent = stats.totalOrders;
    document.getElementById('traditionalOrdersCount').textContent = stats.traditionalOrders;
    document.getElementById('temporalOrdersCount').textContent = stats.temporalOrders;
    document.getElementById('completedOrders').textContent = stats.completedOrders;
    document.getElementById('failedOrders').textContent = stats.failedOrders;
}

function updateTemporalStatus(available) {
    const statusDiv = document.getElementById('temporalStatus');
    if (available) {
        statusDiv.innerHTML = '<strong style="color: #38a169;">✅ Temporal Server Connected</strong> - Full workflow functionality available';
        statusDiv.style.background = '#c6f6d5';
    } else {
        statusDiv.innerHTML = '<strong style="color: #f6ad55;">⚠️ Temporal Server Not Available</strong> - Running in demo mode (simulated workflows)';
        statusDiv.style.background = '#feebc8';
    }
}

function updateOrders(orders) {
    const traditionalDiv = document.getElementById('traditionalOrdersList');
    const temporalDiv = document.getElementById('temporalOrdersList');

    const traditionalOrders = orders.filter(o => o.approach === 'TRADITIONAL')
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    
    const temporalOrders = orders.filter(o => o.approach === 'TEMPORAL')
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    traditionalDiv.innerHTML = traditionalOrders.length > 0
        ? traditionalOrders.map(renderOrder).join('')
        : '<p style="color: #718096; text-align: center; padding: 20px;">No orders yet</p>';

    temporalDiv.innerHTML = temporalOrders.length > 0
        ? temporalOrders.map(renderOrder).join('')
        : '<p style="color: #718096; text-align: center; padding: 20px;">No orders yet</p>';
}

function renderOrder(order) {
    return `
        <div class="order-card">
            <div class="order-header">
                <span class="order-id">${order.orderId}</span>
                <span class="status-badge status-${order.status.toLowerCase()}">${order.status}</span>
            </div>
            <div class="order-details">
                <div><strong>Customer:</strong> ${order.customerId}</div>
                <div><strong>Amount:</strong> $${order.amount.toFixed(2)}</div>
                <div><strong>Created:</strong> ${new Date(order.createdAt).toLocaleTimeString()}</div>
                ${order.completedAt ? `<div><strong>Completed:</strong> ${new Date(order.completedAt).toLocaleTimeString()}</div>` : ''}
                ${order.errorMessage ? `<div style="color: #e53e3e;"><strong>Error:</strong> ${order.errorMessage}</div>` : ''}
                ${order.retryCount > 0 ? `<div><strong>Retries:</strong> ${order.retryCount}</div>` : ''}
            </div>
            <div class="order-history">${order.executionHistory || 'No history'}</div>
        </div>
    `;
}

async function createOrder(type) {
    const customerId = `CUST-${Math.floor(Math.random() * 1000)}`;
    const amount = Math.floor(Math.random() * 900) + 100;

    try {
        const response = await fetch(`/api/orders/${type}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ customerId, amount })
        });

        if (response.ok) {
            refreshDashboard();
        }
    } catch (error) {
        console.error('Error creating order:', error);
    }
}

async function createBulkOrders() {
    for (let i = 0; i < 10; i++) {
        await createOrder('traditional');
        await createOrder('temporal');
        await new Promise(resolve => setTimeout(resolve, 100));
    }
}

async function resetOrders() {
    if (confirm('Are you sure you want to reset all orders?')) {
        try {
            await fetch('/api/orders/reset', { method: 'DELETE' });
            refreshDashboard();
        } catch (error) {
            console.error('Error resetting orders:', error);
        }
    }
}

async function runDemo() {
    // Reset first
    try {
        await fetch('/api/orders/reset', { method: 'DELETE' });
    } catch (error) {
        console.error('Error resetting orders:', error);
    }
    
    // Wait a moment for reset to complete
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // Create demo orders in sequence to show the workflow
    const demoSequence = [
        { type: 'traditional', delay: 0, customer: 'DEMO-001', amount: 99.99 },
        { type: 'temporal', delay: 500, customer: 'DEMO-002', amount: 149.99 },
        { type: 'traditional', delay: 1000, customer: 'DEMO-003', amount: 79.50 },
        { type: 'temporal', delay: 1500, customer: 'DEMO-004', amount: 199.99 },
        { type: 'traditional', delay: 2000, customer: 'DEMO-005', amount: 129.00 },
    ];
    
    for (const order of demoSequence) {
        await new Promise(resolve => setTimeout(resolve, order.delay));
        try {
            await fetch(`/api/orders/${order.type}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    customerId: order.customer, 
                    amount: order.amount 
                })
            });
        } catch (error) {
            console.error('Error creating demo order:', error);
        }
    }
    
    // Refresh dashboard after demo
    setTimeout(refreshDashboard, 1000);
}

window.onload = function() {
    init();
    // Auto-run demo after 2 seconds if no orders exist
    setTimeout(async () => {
        try {
            const response = await fetch('/api/orders');
            const orders = await response.json();
            if (orders.length === 0) {
                runDemo();
            }
        } catch (error) {
            console.error('Error checking orders:', error);
        }
    }, 2000);
};
window.onbeforeunload = () => clearInterval(refreshInterval);
