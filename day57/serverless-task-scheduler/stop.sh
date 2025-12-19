#!/bin/bash

echo "⏹️  Stopping Serverless Task Scheduler..."

# Stop Docker services
docker compose -f docker/docker-compose.yml down 2>/dev/null || docker-compose -f docker/docker-compose.yml down

echo "✅ Services stopped"
