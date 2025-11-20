#!/bin/bash

echo "========================================="
echo "Starting Task Scheduler Metrics"
echo "========================================="

# Check if jar exists
JAR_FILE=$(find target -name "*.jar" -type f 2>/dev/null | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "No JAR file found. Running build first..."
    ./build.sh
    JAR_FILE=$(find target -name "*.jar" -type f | head -1)
fi

echo "Starting application..."
java -jar "$JAR_FILE" &
APP_PID=$!
echo $APP_PID > app.pid

echo ""
echo "Application starting with PID: $APP_PID"
echo ""
echo "Waiting for application to be ready..."

# Wait for app to be ready
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo ""
        echo "========================================="
        echo "Application is ready!"
        echo "========================================="
        echo ""
        echo "Dashboard:    http://localhost:8080"
        echo "Health:       http://localhost:8080/actuator/health"
        echo "Metrics:      http://localhost:8080/actuator/metrics"
        echo "Prometheus:   http://localhost:8080/actuator/prometheus"
        echo "H2 Console:   http://localhost:8080/h2-console"
        echo ""
        echo "API Endpoints:"
        echo "  POST /api/tasks           - Submit a task"
        echo "  GET  /api/tasks           - List all tasks"
        echo "  GET  /api/tasks/stats     - Get task statistics"
        echo "  POST /api/tasks/generate/N - Generate N tasks"
        echo "  GET  /api/metrics/summary - Get metrics summary"
        echo "  GET  /api/metrics/timers  - Get timer metrics"
        echo ""
        exit 0
    fi
    sleep 1
    echo -n "."
done

echo ""
echo "Warning: Application may not have started properly"
echo "Check logs with: tail -f logs/app.log"
