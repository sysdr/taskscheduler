#!/bin/bash

echo "Starting Distributed Task Scheduler System..."

cd "$(dirname "$0")"

# Start infrastructure
echo "Starting infrastructure services..."
cd deployment/docker
docker-compose up -d
cd ../..

# Wait for services
echo "Waiting for services to be ready..."
sleep 15

# Start applications
echo "Starting Scheduler Core..."
java -jar scheduler-core/target/scheduler-core-1.0.0.jar &
CORE_PID=$!

sleep 10

echo "Starting Scheduler Worker..."
java -jar scheduler-worker/target/scheduler-worker-1.0.0.jar &
WORKER_PID=$!

sleep 5

echo "Starting Scheduler API..."
java -jar scheduler-api/target/scheduler-api-1.0.0.jar &
API_PID=$!

sleep 5

echo "Starting Scheduler UI..."
java -jar scheduler-ui/target/scheduler-ui-1.0.0.jar &
UI_PID=$!

echo ""
echo "========================================="
echo "System Started Successfully!"
echo "========================================="
echo "Dashboard: http://localhost:8080"
echo "Scheduler Core: http://localhost:8081"
echo "Worker: http://localhost:8082"
echo "API: http://localhost:8083"
echo "Prometheus: http://localhost:9090"
echo "Grafana: http://localhost:3000 (admin/admin)"
echo "========================================="
echo ""
echo "PIDs: Core=$CORE_PID Worker=$WORKER_PID API=$API_PID UI=$UI_PID"
