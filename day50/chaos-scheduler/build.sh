#!/bin/bash
echo "Building Chaos Scheduler..."
mvn clean package -DskipTests
echo "Build complete!"
