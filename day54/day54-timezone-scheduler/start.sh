#!/bin/bash
echo "Starting Timezone-Aware Scheduler..."
if [ -f "./mvnw" ]; then
    ./mvnw spring-boot:run
else
    mvn spring-boot:run
fi
