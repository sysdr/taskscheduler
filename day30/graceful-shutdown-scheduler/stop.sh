#!/bin/bash

echo "🛑 Stopping Graceful Shutdown Task Scheduler..."

# Find and kill the Java process
PID=$(pgrep -f "graceful-shutdown-scheduler-1.0.0.jar" || true)

if [ -n "$PID" ]; then
    echo "📍 Found application running with PID: $PID"
    echo "🔄 Sending graceful shutdown signal (SIGTERM)..."
    kill -TERM $PID
    
    # Wait up to 30 seconds for graceful shutdown
    for i in {1..30}; do
        if ! kill -0 $PID 2>/dev/null; then
            echo "✅ Application stopped gracefully"
            exit 0
        fi
        echo "⏳ Waiting for graceful shutdown... ($i/30)"
        sleep 1
    done
    
    echo "⚠️ Graceful shutdown timeout reached, forcing termination..."
    kill -KILL $PID 2>/dev/null || true
    echo "💀 Application force-stopped"
else
    echo "ℹ️ No running application found"
fi
