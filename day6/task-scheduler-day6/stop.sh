#!/bin/bash

echo "🛑 Stopping Task Scheduler Day 6..."

# Kill Java processes
pkill -f "task-scheduler-day6"

# Stop Docker containers
if command -v docker-compose >/dev/null 2>&1; then
    echo "🐘 Stopping PostgreSQL..."
    docker-compose down
fi

echo "✅ Task Scheduler Day 6 stopped"
