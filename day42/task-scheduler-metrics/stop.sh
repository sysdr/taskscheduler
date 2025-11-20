#!/bin/bash

echo "Stopping Task Scheduler Metrics..."

if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null 2>&1; then
        kill $PID
        echo "Application stopped (PID: $PID)"
    else
        echo "Application not running"
    fi
    rm -f app.pid
else
    # Try to find and kill by port
    PID=$(lsof -t -i:8080 2>/dev/null)
    if [ ! -z "$PID" ]; then
        kill $PID
        echo "Application stopped (PID: $PID)"
    else
        echo "No application found running on port 8080"
    fi
fi
