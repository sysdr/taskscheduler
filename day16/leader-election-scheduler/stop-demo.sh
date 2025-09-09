#!/bin/bash

echo "🛑 Stopping demo instances..."

# Kill instances using PID files
for port in 8080 8081 8082; do
    if [ -f instance-$port.pid ]; then
        pid=$(cat instance-$port.pid)
        echo "   Stopping instance $port (PID: $pid)"
        kill $pid 2>/dev/null
        rm instance-$port.pid
        rm -f instance-$port.log
    fi
done

echo "✅ Demo stopped"
