#!/bin/bash

# Get the script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "🛑 Stopping Event-Driven Scheduler..."

# Stop Spring Boot (if running)
pkill -f "event-driven-scheduler"

# Stop Docker services
echo "🐳 Stopping Docker services..."
DOCKER_DIR="$SCRIPT_DIR/docker"
if [ -d "$DOCKER_DIR" ]; then
    cd "$DOCKER_DIR"
    docker compose down
else
    echo "⚠️  Docker directory not found: $DOCKER_DIR"
fi

echo "✅ All services stopped"
