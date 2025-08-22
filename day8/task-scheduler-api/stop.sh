#!/bin/bash

echo "🛑 Stopping Task Scheduler API..."

# Stop Docker containers if running
if [ -f "docker/docker-compose.yml" ] && command -v docker-compose >/dev/null 2>&1; then
    if docker-compose -f docker/docker-compose.yml ps -q >/dev/null 2>&1; then
        echo "🐳 Stopping Docker containers..."
        cd docker
        docker-compose down
        cd ..
        echo "✅ Docker containers stopped"
    fi
fi

# Stop local application
if [ -f ".app.pid" ]; then
    APP_PID=$(cat .app.pid)
    if ps -p $APP_PID > /dev/null 2>&1; then
        echo "🛑 Stopping application (PID: $APP_PID)..."
        kill $APP_PID
        sleep 3
        if ps -p $APP_PID > /dev/null 2>&1; then
            echo "⚠️  Force killing application..."
            kill -9 $APP_PID
        fi
        echo "✅ Application stopped"
    else
        echo "ℹ️  Application not running"
    fi
    rm -f .app.pid
else
    echo "ℹ️  No PID file found"
fi

# Kill any remaining Java processes on port 8080
if command -v lsof >/dev/null 2>&1; then
    PID_ON_PORT=$(lsof -ti:8080)
    if [ ! -z "$PID_ON_PORT" ]; then
        echo "🛑 Killing process on port 8080..."
        kill $PID_ON_PORT 2>/dev/null || true
    fi
fi

echo "✅ Task Scheduler API stopped"
