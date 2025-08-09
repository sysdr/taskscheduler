#!/bin/bash

# Resolve repository root so script works from any cwd
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$REPO_ROOT"

echo "🛑 Stopping @Scheduled Duplication Demo"

# Stop Docker containers if running
if [ "$1" = "docker" ] || docker ps | grep -q "scheduled-duplication-demo"; then
    echo "🐳 Stopping Docker containers..."
    cd "$REPO_ROOT/docker"
    docker-compose down --remove-orphans
    echo "✅ Docker containers stopped"
fi

# Stop local Java processes
echo "💻 Stopping local Java processes..."

for pid_file in "$REPO_ROOT"/scripts/instance*.pid; do
    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null; then
            echo "🛑 Stopping process $pid"
            kill $pid
            # Wait for graceful shutdown
            sleep 3
            # Force kill if still running
            if ps -p $pid > /dev/null; then
                echo "🔪 Force killing process $pid"
                kill -9 $pid
            fi
        fi
        rm "$pid_file"
    fi
done

# Clean up any remaining processes
pkill -f "scheduled-duplication-demo" || true

echo "✅ All instances stopped"
echo "🧹 Logs preserved in $REPO_ROOT/logs/ directory"
