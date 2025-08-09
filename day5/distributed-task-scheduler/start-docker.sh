#!/bin/bash

echo "🐳 Starting Distributed Task Scheduler with Docker"

cd docker

echo "🔨 Building and starting all services..."
docker-compose up --build -d

echo "⏳ Waiting for services to be healthy..."
sleep 30

echo "✅ Services started!"
echo ""
echo "🎯 Demo URLs:"
echo "📊 Instance 1 Dashboard: http://localhost:8080/scheduler/dashboard"
echo "📊 Instance 2 Dashboard: http://localhost:8081/scheduler/dashboard"
echo "📈 Instance 1 Health: http://localhost:8080/actuator/health"
echo "📈 Instance 2 Health: http://localhost:8081/actuator/health"
echo ""
echo "🔍 To view logs: docker-compose logs -f"
echo "🛑 To stop: ./stop-docker.sh"

cd ..
