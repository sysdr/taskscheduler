#!/bin/bash

echo "🛑 Stopping Dynamic Task Scheduler Application..."

if [ -f .app.pid ]; then
    PID=$(cat .app.pid)
    if ps -p $PID > /dev/null; then
        echo "📱 Stopping application (PID: $PID)..."
        kill $PID
        sleep 5
        
        # Force kill if still running
        if ps -p $PID > /dev/null; then
            echo "🔨 Force stopping application..."
            kill -9 $PID
        fi
        
        echo "✅ Application stopped successfully"
    else
        echo "⚠️ Application was not running"
    fi
    rm -f .app.pid
else
    echo "⚠️ No PID file found, attempting to stop any running Spring Boot processes..."
    pkill -f "spring-boot:run"
fi

echo "�� Cleanup complete"
