let taskChart;

function initializeCharts(statusData) {
    const ctx = document.getElementById('taskChart').getContext('2d');
    
    const data = {
        labels: ['Queued', 'Processing', 'Completed', 'Failed'],
        datasets: [{
            data: [
                statusData.QUEUED || 0,
                statusData.PROCESSING || 0,
                statusData.COMPLETED || 0,
                statusData.FAILED || 0
            ],
            backgroundColor: [
                '#ffa726',
                '#42a5f5',
                '#66bb6a',
                '#ef5350'
            ],
            borderWidth: 0,
            hoverOffset: 10
        }]
    };

    taskChart = new Chart(ctx, {
        type: 'doughnut',
        data: data,
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        usePointStyle: true,
                        font: {
                            size: 12,
                            family: 'Inter'
                        }
                    }
                }
            },
            animation: {
                animateScale: true,
                animateRotate: true
            }
        }
    });
}

function refreshData() {
    const button = document.querySelector('.refresh-btn');
    const icon = button.querySelector('i');
    
    // Add spinning animation
    icon.classList.add('fa-spin');
    button.disabled = true;
    
    // Simulate refresh (in real app, this would be an AJAX call)
    setTimeout(() => {
        window.location.reload();
    }, 500);
}

// Auto-refresh every 10 seconds
setInterval(() => {
    fetch('/api/stats')
        .then(response => response.json())
        .then(data => {
            updateStats(data);
        })
        .catch(error => console.log('Auto-refresh failed:', error));
}, 10000);

function updateStats(data) {
    // Update stat numbers
    document.querySelector('.stat-card.total .stat-number').textContent = data.processedCount;
    
    if (data.statusCounts) {
        document.querySelector('.stat-card.queued .stat-number').textContent = data.statusCounts.QUEUED || 0;
        document.querySelector('.stat-card.processing .stat-number').textContent = data.statusCounts.PROCESSING || 0;
        document.querySelector('.stat-card.completed .stat-number').textContent = data.statusCounts.COMPLETED || 0;
        document.querySelector('.stat-card.failed .stat-number').textContent = data.statusCounts.FAILED || 0;
        
        // Update chart
        if (taskChart) {
            taskChart.data.datasets[0].data = [
                data.statusCounts.QUEUED || 0,
                data.statusCounts.PROCESSING || 0,
                data.statusCounts.COMPLETED || 0,
                data.statusCounts.FAILED || 0
            ];
            taskChart.update();
        }
    }
}

// Add some visual feedback
document.addEventListener('DOMContentLoaded', function() {
    // Animate stat cards on load
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            card.style.transition = 'all 0.5s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
});
