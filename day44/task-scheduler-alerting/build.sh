#!/bin/bash

echo "Building Task Scheduler Alerting System..."

# Build with Maven
mvn clean package -DskipTests

echo "✓ Build completed successfully!"
