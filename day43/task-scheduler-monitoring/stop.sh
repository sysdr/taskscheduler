#!/bin/bash
set -e

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Stopping Task Scheduler Monitoring Stack..."

# Stop Spring Boot application
if [ -f "$SCRIPT_DIR/app.pid" ]; then
    PID=$(cat "$SCRIPT_DIR/app.pid")
    if ps -p $PID > /dev/null 2>&1; then
        kill $PID 2>/dev/null || true
        echo "Application stopped (PID: $PID)"
    else
        echo "Application process not found (PID: $PID)"
    fi
    rm -f "$SCRIPT_DIR/app.pid"
else
    echo "No app.pid file found"
fi

# Stop Docker containers
cd "$SCRIPT_DIR"
docker-compose down
echo "Monitoring stack stopped"
