#!/bin/bash
echo "🎬 Starting Demo with Dashboard Validation..."

# Start application in background
echo "Starting application..."
if [ -f "mvnw" ]; then
    ./mvnw spring-boot:run > demo.log 2>&1 &
else
    mvn spring-boot:run > demo.log 2>&1 &
fi

APP_PID=$!
echo "Application PID: $APP_PID"

# Wait for application to be ready
echo "Waiting for application to start (max 60 seconds)..."
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✓ Application is ready!"
        break
    fi
    echo "  Waiting... ($i/30)"
    sleep 2
    if [ $i -eq 30 ]; then
        echo "❌ Application health check failed"
        kill $APP_PID 2>/dev/null
        echo "Check demo.log for errors:"
        cat demo.log
        exit 1
    fi
done

echo ""
echo "🎯 Creating demo tasks to populate dashboard..."

# Create and process multiple tasks for dashboard demonstration
for i in {1..5}; do
    echo "Creating task $i..."
    RESPONSE=$(curl -s -X POST http://localhost:8080/api/tasks \
        -H "Content-Type: application/json" \
        -d "{\"taskName\":\"demo-task-$i\",\"executionDetails\":\"Demo task number $i\"}")
    
    TASK_ID=$(echo "$RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
    echo "  Created task ID: $TASK_ID"
    
    # Start the task
    echo "  Starting task $TASK_ID..."
    curl -s -X POST http://localhost:8080/api/tasks/$TASK_ID/start > /dev/null
    
    # Complete or fail tasks for variety
    if [ $((i % 3)) -eq 0 ]; then
        echo "  Failing task $TASK_ID..."
        curl -s -X POST "http://localhost:8080/api/tasks/$TASK_ID/fail?errorMessage=Demo%20failure%20for%20task%20$i" > /dev/null
    elif [ $((i % 2)) -eq 0 ]; then
        echo "  Completing task $TASK_ID..."
        curl -s -X POST http://localhost:8080/api/tasks/$TASK_ID/complete > /dev/null
    else
        echo "  Leaving task $TASK_ID in RUNNING state..."
    fi
    
    sleep 1
done

echo ""
echo "📊 Dashboard Metrics Validation:"
STATS=$(curl -s http://localhost:8080/api/tasks/statistics)
echo "$STATS"

echo ""
echo "✅ Demo setup complete!"
echo ""
echo "🌐 Access Points:"
echo "  Dashboard:    http://localhost:8080"
echo "  H2 Console:   http://localhost:8080/h2-console"
echo "  API Stats:    http://localhost:8080/api/tasks/statistics"
echo "  Health Check: http://localhost:8080/actuator/health"
echo "  Metrics:      http://localhost:8080/actuator/metrics"
echo ""
echo "📝 Dashboard should now show:"
echo "  - Multiple tasks in different states"
echo "  - Status distribution chart with data"
echo "  - Non-zero metrics counters"
echo "  - Interactive task actions"
echo ""
echo "Press Ctrl+C to stop the application"
echo "Application logs: demo.log"

# Keep application running
wait $APP_PID
