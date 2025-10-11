#!/bin/bash

if [ -f app.pid ]; then
    PID=$(cat app.pid)
    echo "🛑 Stopping Task Scheduler (PID: $PID)..."
    kill $PID
    rm app.pid
    echo "✅ Application stopped."
else
    echo "❌ No running application found."
fi
