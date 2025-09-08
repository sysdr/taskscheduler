#!/bin/bash

echo "🛑 Stopping Redlock Task Scheduler..."

# Stop Spring Boot application
pkill -f "redlock-task-scheduler"

# Stop Redis cluster
cd docker
docker-compose down
cd ..

echo "✅ All services stopped"
