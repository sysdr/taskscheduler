#!/bin/bash

echo "Starting Centralized Logging System..."

# Start Elasticsearch and Logstash
echo "Starting Elasticsearch and Logstash..."
cd docker
docker-compose up -d
cd ..

echo "Waiting for Elasticsearch to be ready..."
until curl -s http://localhost:9200/_cluster/health | grep -q '"status":"green"\|"status":"yellow"'; do
    echo "Waiting..."
    sleep 5
done

echo "✓ Elasticsearch is ready!"

# Start multiple scheduler instances
echo "Starting scheduler instances..."

INSTANCE_ID=01 SERVER_PORT=8080 java -jar target/centralized-logging-1.0.0.jar > logs/instance-01.log 2>&1 &
echo $! > instance-01.pid

INSTANCE_ID=02 SERVER_PORT=8081 java -jar target/centralized-logging-1.0.0.jar > logs/instance-02.log 2>&1 &
echo $! > instance-02.pid

INSTANCE_ID=03 SERVER_PORT=8082 java -jar target/centralized-logging-1.0.0.jar > logs/instance-03.log 2>&1 &
echo $! > instance-03.pid

echo "✓ All instances started!"
echo ""
echo "Instance 01: http://localhost:8080"
echo "Instance 02: http://localhost:8081"
echo "Instance 03: http://localhost:8082"
echo "Dashboard: http://localhost:8080/dashboard"
echo "Elasticsearch: http://localhost:9200"
