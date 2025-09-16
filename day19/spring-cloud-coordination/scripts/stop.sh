#!/bin/bash

echo "🛑 Stopping Spring Cloud Coordination services..."

# Kill Java processes
pkill -f "spring-cloud-coordination"

# Stop Redis container if exists
docker stop redis-coordination 2>/dev/null || true
docker rm redis-coordination 2>/dev/null || true

echo "✅ All services stopped"
