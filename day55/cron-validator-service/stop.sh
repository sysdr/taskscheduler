#!/bin/bash

echo "🛑 Stopping Cron Validator Service..."

if [ -f "app.pid" ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null; then
        kill $PID
        echo "✅ Application stopped (PID: $PID)"
        rm app.pid
    else
        echo "⚠️  Process not running"
        rm app.pid
    fi
else
    echo "⚠️  PID file not found"
fi
