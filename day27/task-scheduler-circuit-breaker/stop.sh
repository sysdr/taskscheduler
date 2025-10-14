#!/bin/bash
set -e

echo "🛑 Stopping Task Scheduler..."

# Stop Docker Compose services
if command -v docker-compose &> /dev/null; then
    docker-compose down
    echo "🐳 Docker services stopped"
fi

# Kill Java processes
pkill -f "task-scheduler-circuit-breaker" || true

echo "✅ All services stopped"
