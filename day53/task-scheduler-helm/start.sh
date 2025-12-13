#!/bin/bash
set -e

echo "Starting Task Scheduler with Docker Compose..."

# Start services
docker compose up -d

echo "Waiting for services to be healthy..."
sleep 15

echo ""
echo "✓ Task Scheduler started successfully!"
echo ""
echo "Dashboard: http://localhost:8080"
echo "API: http://localhost:8080/api/tasks"
echo "Health: http://localhost:8080/actuator/health"
echo "Metrics: http://localhost:8080/actuator/prometheus"
echo ""
echo "To view logs: docker compose logs -f app"
echo "To stop: ./stop.sh"
