#!/bin/bash
echo "🛑 Stopping Timeout Task Scheduler..."

# Find and kill the Spring Boot process
pkill -f "timeout-task-scheduler"

echo "✅ Application stopped successfully!"
