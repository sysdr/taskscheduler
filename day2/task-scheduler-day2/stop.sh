#!/bin/bash

echo "🛑 Stopping Task Scheduler Application"
echo "======================================"

if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null; then
        echo "🔸 Stopping application (PID: $PID)..."
        kill $PID
        sleep 5
        
        if ps -p $PID > /dev/null; then
            echo "🔸 Force stopping application..."
            kill -9 $PID
        fi
        
        rm app.pid
        echo "✅ Application stopped successfully"
    else
        echo "⚠️ Application not running"
        rm app.pid
    fi
else
    echo "⚠️ No PID file found"
fi

# Kill any remaining Java processes for this project
pkill -f "task-scheduler-day2" || true

echo "🧹 Cleanup completed"
