#!/bin/bash

echo "🛑 Stopping ThreadPoolTaskScheduler Demo..."

# Stop Docker containers if running
if [ -f "docker-compose.yml" ] && command -v docker-compose &> /dev/null; then
    echo "🐳 Stopping Docker containers..."
    docker-compose down
fi

# Stop local Java application if running
if [ -f "app.pid" ]; then
    echo "☕ Stopping local Java application..."
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null; then
        kill $PID
        echo "✅ Application stopped (PID: $PID)"
    else
        echo "ℹ️  Application was not running"
    fi
    rm app.pid
fi

# Clean up any remaining processes
pkill -f "thread-pool-scheduler" 2>/dev/null || true

echo "✅ Cleanup complete!"
