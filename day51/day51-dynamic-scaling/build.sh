#!/bin/bash
echo "Building..."
mvn clean package -DskipTests
docker build -t dynamic-scaling .
echo "✅ Build complete!"
