#!/bin/bash

echo "🚀 Starting Spring Retry Task Scheduler..."

# Start with Docker Compose
cd docker
docker-compose up -d

echo "✅ Services started!"
echo "📊 Application: http://localhost:8080"
echo "📊 H2 Console: http://localhost:8080/h2-console"
echo "📊 Actuator: http://localhost:8080/actuator"
echo "📊 Prometheus: http://localhost:9090"
echo "📊 Grafana: http://localhost:3000 (admin/admin)"
