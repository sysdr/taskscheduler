#!/bin/bash

echo "Stopping Task Scheduler Alerting System..."

# Stop Spring Boot
pkill -f task-scheduler-alerting

# Stop Docker services
docker compose down

echo "✓ All services stopped!"
