#!/bin/bash

echo "🛑 Stopping Distributed Task Scheduler"

if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null 2>&1; then
        kill $PID
        echo "✅ Application stopped (PID: $PID)"
        rm app.pid
    else
        echo "⚠️  Application not running"
        rm app.pid
    fi
else
    echo "⚠️  PID file not found. Trying to find and kill java processes..."
    pkill -f "distributed-task-scheduler" || echo "No processes found"
fi

echo "🧹 Cleanup complete"
