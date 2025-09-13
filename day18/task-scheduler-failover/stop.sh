#!/bin/bash

if [ "$1" == "docker" ]; then
    echo "🐳 Stopping Docker containers..."
    cd docker
    docker-compose down
    echo "✅ All containers stopped!"
else
    echo "🛑 Stopping Task Scheduler..."
    pkill -f "task-scheduler-failover" || true
    echo "✅ Application stopped!"
fi
