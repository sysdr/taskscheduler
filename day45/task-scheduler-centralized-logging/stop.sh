#!/bin/bash

echo "Stopping Centralized Logging System..."

# Stop scheduler instances
if [ -f instance-01.pid ]; then
    kill $(cat instance-01.pid) 2>/dev/null
    rm instance-01.pid
fi

if [ -f instance-02.pid ]; then
    kill $(cat instance-02.pid) 2>/dev/null
    rm instance-02.pid
fi

if [ -f instance-03.pid ]; then
    kill $(cat instance-03.pid) 2>/dev/null
    rm instance-03.pid
fi

# Stop Docker containers
cd docker
docker-compose down
cd ..

echo "✓ All services stopped!"
