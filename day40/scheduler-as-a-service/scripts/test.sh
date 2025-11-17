#!/bin/bash

echo "Testing Scheduler Service..."

# Wait for services
sleep 5

# Test health endpoint
echo "Testing health endpoint..."
curl http://localhost:8080/actuator/health

echo ""
echo "Testing payment service task submission..."
curl -X POST http://localhost:8081/api/payments/reconcile

echo ""
echo "Testing notification service task submission..."
curl -X POST http://localhost:8082/api/notifications/send-batch

echo ""
echo "Test completed!"
