#!/bin/bash
echo "Building Timezone-Aware Scheduler..."
if [ -f "./mvnw" ]; then
    ./mvnw clean package -DskipTests
else
    mvn clean package -DskipTests
fi
echo "✓ Build complete!"
