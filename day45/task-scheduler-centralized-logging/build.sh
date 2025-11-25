#!/bin/bash

echo "Building Task Scheduler with Centralized Logging..."

# Install dependencies and build
mvn clean install -DskipTests

echo "✓ Build completed successfully!"
