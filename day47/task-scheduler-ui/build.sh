#!/bin/bash
echo "Building Task Scheduler UI..."
mvn clean package -DskipTests
echo "Build complete!"
