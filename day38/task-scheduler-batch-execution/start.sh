#!/bin/bash

echo "Starting Day 38: Batching Task Executions..."
echo "============================================"

# Check if already running
if lsof -Pi :8038 -sTCP:LISTEN -t >/dev/null ; then
    echo "Application already running on port 8038"
    exit 1
fi

# Start application
echo "Starting application..."
nohup java -jar target/batch-execution-1.0.0.jar > app.log 2>&1 &
APP_PID=$!
echo $APP_PID > app.pid

echo "Waiting for application to start..."
for i in {1..30}; do
    if curl -s http://localhost:8038/actuator/health > /dev/null 2>&1; then
        echo ""
        echo "✓ Application started successfully!"
        echo ""
        echo "Access points:"
        echo "  Dashboard:    http://localhost:8038"
        echo "  Health:       http://localhost:8038/actuator/health"
        echo "  Metrics:      http://localhost:8038/actuator/metrics"
        echo "  Prometheus:   http://localhost:8038/actuator/prometheus"
        echo "  H2 Console:   http://localhost:8038/h2-console"
        echo ""
        echo "Logs: tail -f app.log"
        exit 0
    fi
    sleep 1
done

echo "Error: Application failed to start"
exit 1
