#!/bin/bash

echo "=== Stopping Task Scheduler Leader Election Services ==="

# Kill application instances
if [ -f .app1.pid ]; then
    APP1_PID=$(cat .app1.pid)
    echo "Stopping Application Instance 1 (PID: $APP1_PID)..."
    kill $APP1_PID 2>/dev/null || true
    rm .app1.pid
fi

if [ -f .app2.pid ]; then
    APP2_PID=$(cat .app2.pid)
    echo "Stopping Application Instance 2 (PID: $APP2_PID)..."
    kill $APP2_PID 2>/dev/null || true
    rm .app2.pid
fi

# Stop Docker containers
echo "Stopping MySQL container..."
cd docker
docker-compose down

echo "✅ All services stopped!"
