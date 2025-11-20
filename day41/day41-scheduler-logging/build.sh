#!/bin/bash
echo "Building Day 41: Scheduler Logging..."

# Create logs directory
mkdir -p logs

# Build project
./mvnw clean package -DskipTests

echo "Build complete!"
