#!/bin/bash
echo "🛑 Stopping Idempotent Task Scheduler..."

if docker-compose ps > /dev/null 2>&1; then
    docker-compose down
    echo "🐳 Docker containers stopped"
else
    echo "ℹ️  No Docker containers running"
fi

# Kill any Java processes
pkill -f "idempotent-task-scheduler" || true

echo "✅ Application stopped"
