#!/bin/bash

echo "Testing Optimistic Lock Scheduler Endpoints"
echo "=========================================="

echo "1. Testing Health Endpoint:"
curl -s http://localhost:8080/actuator/health
echo -e "\n"

echo "2. Testing Task Statistics:"
curl -s http://localhost:8080/api/tasks/statistics
echo -e "\n"

echo "3. Testing Task Metrics:"
echo "   Tasks Processed:"
curl -s http://localhost:8080/actuator/metrics/tasks.processed.total
echo -e "\n"

echo "   Tasks Failed:"
curl -s http://localhost:8080/actuator/metrics/tasks.failed.total
echo -e "\n"

echo "   Optimistic Lock Success:"
curl -s http://localhost:8080/actuator/metrics/optimistic.lock.success.total
echo -e "\n"

echo "   Optimistic Lock Conflicts:"
curl -s http://localhost:8080/actuator/metrics/optimistic.lock.conflicts.total
echo -e "\n"

echo "4. Current Tasks:"
curl -s http://localhost:8080/api/tasks
echo -e "\n"

echo "Test completed!"
