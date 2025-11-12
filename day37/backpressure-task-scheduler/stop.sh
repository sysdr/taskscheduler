#!/bin/bash
echo "Stopping Backpressure Task Scheduler..."

# Stop application (if running)
pkill -f "spring-boot:run"

# Stop Docker services
cd docker
docker-compose down
cd ..

echo "✓ All services stopped"
