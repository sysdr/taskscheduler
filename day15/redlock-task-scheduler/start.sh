#!/bin/bash

echo "🚀 Starting Redlock Task Scheduler..."

# Start Redis cluster
echo "🔴 Starting Redis cluster..."
cd docker
docker-compose up -d
cd ..

# Wait for Redis
sleep 10

# Start application
echo "🔨 Starting Spring Boot application..."
./gradle-8.5/bin/gradle bootRun
