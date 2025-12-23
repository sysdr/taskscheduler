#!/bin/bash

echo "🛑 Stopping Workflow Comparison System..."

# Stop Docker containers if running
cd docker
docker-compose down
cd ..

# Kill Spring Boot process
pkill -f workflow-comparison

echo "✅ All services stopped!"
