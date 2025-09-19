#!/bin/bash

echo "🛑 Stopping Task Execution Tracker..."

if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null 2>&1; then
        kill $PID
        echo "✅ Application stopped (PID: $PID)"
        rm app.pid
    else
        echo "⚠️ Application was not running"
        rm app.pid
    fi
else
    echo "⚠️ PID file not found"
fi
