#!/bin/bash

echo "🛑 Stopping Task Scheduler..."

if command -v docker &> /dev/null; then
    cd docker
    docker-compose down
    echo "✅ Docker services stopped"
else
    # Kill Java processes
    pkill -f "redis-distributed-locks"
    echo "✅ Application stopped"
fi
