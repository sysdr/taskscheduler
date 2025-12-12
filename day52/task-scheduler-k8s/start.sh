#!/bin/bash
set -e

echo "Starting Task Scheduler with Docker Compose..."
docker-compose up -d

echo ""
echo "✅ Services started!"
echo "📊 Dashboard: http://localhost:8080"
echo "📈 Prometheus: http://localhost:9090"
echo "💚 Health: http://localhost:8080/actuator/health"
echo ""
echo "View logs: docker-compose logs -f"
