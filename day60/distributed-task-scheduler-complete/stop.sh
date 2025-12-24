#!/bin/bash

echo "Stopping Distributed Task Scheduler System..."

cd "$(dirname "$0")"

# Stop Java applications
pkill -f scheduler-core
pkill -f scheduler-worker
pkill -f scheduler-api
pkill -f scheduler-ui

# Stop Docker services
cd deployment/docker
docker-compose down
cd ../..

echo "System stopped successfully!"
