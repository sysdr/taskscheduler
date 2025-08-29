#!/bin/bash

echo "🛑 Stopping all instances..."

# Stop all instances
if [ -f pids/instance1.pid ]; then
    PID1=$(cat pids/instance1.pid)
    if kill -0 $PID1 2>/dev/null; then
        kill $PID1
        echo "Stopped Instance 1 (PID: $PID1)"
    fi
    rm -f pids/instance1.pid
fi

if [ -f pids/instance2.pid ]; then
    PID2=$(cat pids/instance2.pid)
    if kill -0 $PID2 2>/dev/null; then
        kill $PID2
        echo "Stopped Instance 2 (PID: $PID2)"
    fi
    rm -f pids/instance2.pid
fi

if [ -f pids/instance3.pid ]; then
    PID3=$(cat pids/instance3.pid)
    if kill -0 $PID3 2>/dev/null; then
        kill $PID3
        echo "Stopped Instance 3 (PID: $PID3)"
    fi
    rm -f pids/instance3.pid
fi

echo "✅ All instances stopped!"
