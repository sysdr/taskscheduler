#!/bin/bash

echo "Building Distributed Task Scheduler..."

cd "$(dirname "$0")"

# Build all modules
mvn clean install -DskipTests

echo "Build complete!"
