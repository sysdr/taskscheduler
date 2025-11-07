#!/bin/bash
set -e

echo "🐳 Starting services with Docker Compose..."

cd docker
docker-compose up -d

echo ""
echo "✅ All services started!"
echo "📊 Dashboard: http://localhost:8082"
echo "📤 Producer API: http://localhost:8080"
echo "🐰 RabbitMQ Management: http://localhost:15672"
echo ""
echo "To view logs: docker-compose logs -f"
echo "To stop: docker-compose down"
