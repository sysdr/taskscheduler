#!/bin/bash

echo "🛑 Stopping Async Task Scheduler..."

# Find and kill the Spring Boot process
PID=$(ps aux | grep '[a]sync-task-scheduler-1.0.0.jar' | awk '{print $2}')

if [ -n "$PID" ]; then
    echo "Stopping process $PID..."
    kill $PID
    echo "✅ Application stopped"
else
    echo "No running application found"
fi
