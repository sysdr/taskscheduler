#!/bin/bash
echo "Testing Task Scheduler API..."

# Wait for application to be ready
echo "Waiting for application to start..."
sleep 15

# Submit test tasks
echo "Submitting test tasks..."
for i in {1..10}; do
    curl -X POST http://localhost:8080/api/tasks \
         -H "Content-Type: application/json" \
         -d "{\"name\": \"Test Task $i\", \"type\": \"EMAIL\"}" \
         -s > /dev/null
    echo "Submitted task $i"
    sleep 1
done

echo ""
echo "Checking metrics..."
curl -s http://localhost:8080/api/tasks/stats | python3 -m json.tool

echo ""
echo "Test complete! Check the following:"
echo "1. Application Dashboard: http://localhost:8080"
echo "2. Prometheus Targets: http://localhost:9090/targets"
echo "3. Grafana Dashboard: http://localhost:3000"
