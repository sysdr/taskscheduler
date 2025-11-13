#!/bin/bash

echo "Stopping Day 38: Batching Task Executions..."

if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if kill -0 $PID 2>/dev/null; then
        kill $PID
        echo "Application stopped (PID: $PID)"
        rm app.pid
    else
        echo "Application not running"
        rm app.pid
    fi
else
    echo "PID file not found"
fi
