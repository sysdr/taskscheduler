#!/bin/bash

echo "🛑 Stopping Enhanced Task Scheduler..."

# Find and kill Java processes running the application
PIDS=$(ps aux | grep "enhanced-task-scheduler" | grep -v grep | awk '{print $2}')

if [ -z "$PIDS" ]; then
    echo "✅ No running instances found"
else
    echo "🔍 Found running instances with PIDs: $PIDS"
    echo "🔄 Stopping processes..."
    
    for PID in $PIDS; do
        kill $PID 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "✅ Stopped process $PID"
        else
            echo "❌ Failed to stop process $PID"
        fi
    done
    
    # Wait a bit and force kill if necessary
    sleep 3
    PIDS=$(ps aux | grep "enhanced-task-scheduler" | grep -v grep | awk '{print $2}')
    
    if [ ! -z "$PIDS" ]; then
        echo "⚠️  Some processes still running, force killing..."
        for PID in $PIDS; do
            kill -9 $PID 2>/dev/null
            echo "✅ Force killed process $PID"
        done
    fi
fi

echo "✅ Enhanced Task Scheduler stopped"
