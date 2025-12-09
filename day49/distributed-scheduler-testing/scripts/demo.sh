#!/bin/bash
cd "$(dirname "$0")/.."
docker run -d --name redis-demo -p 6379:6379 redis:7-alpine
sleep 2
mvn clean package -DskipTests
mvn spring-boot:run &
echo "Dashboard: http://localhost:8080"
echo "Press Ctrl+C to stop"
wait
