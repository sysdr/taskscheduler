#!/bin/bash

echo "Testing Centralized Logging System..."

# Wait for services to be ready
sleep 10

echo "1. Testing Elasticsearch connection..."
curl -s http://localhost:9200/_cluster/health | jq .

echo -e "\n2. Testing log search API..."
curl -s http://localhost:8080/api/logs/search?size=5 | jq .

echo -e "\n3. Testing log stats API..."
curl -s http://localhost:8080/api/logs/stats | jq .

echo -e "\n4. Testing log filtering..."
curl -s "http://localhost:8080/api/logs/search?level=ERROR&size=5" | jq .

echo -e "\n✓ All tests completed!"
echo "Dashboard: http://localhost:8080/dashboard"
