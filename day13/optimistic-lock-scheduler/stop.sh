#!/bin/bash

echo "🛑 Stopping Optimistic Lock Task Scheduler..."

# Stop Spring Boot application
PID=$(pgrep -f "optimistic-lock-scheduler")
if [ ! -z "$PID" ]; then
    echo "Stopping Spring Boot application (PID: $PID)..."
    kill $PID
    sleep 3
    
    # Force kill if still running
    if pgrep -f "optimistic-lock-scheduler" > /dev/null; then
        echo "Force killing application..."
        pkill -9 -f "optimistic-lock-scheduler"
    fi
fi

# Stop Docker containers
if [ -f "docker/docker-compose.yml" ]; then
    echo "Stopping Docker containers..."
    cd docker && docker-compose down
fi

echo "✅ Cleanup completed!"
