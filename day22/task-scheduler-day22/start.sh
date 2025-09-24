#!/bin/bash
echo "🚀 Starting Task Scheduler Application..."

# Check if mvnw exists, otherwise use mvn
if [ -f "mvnw" ]; then
    echo "Using Maven wrapper..."
    ./mvnw spring-boot:run
else
    echo "Using system Maven..."
    mvn spring-boot:run
fi
