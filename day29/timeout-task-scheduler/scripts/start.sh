#!/bin/bash
echo "🚀 Starting Timeout Task Scheduler..."

# Start the application
./mvnw spring-boot:run &

# Wait for startup
sleep 10

echo "✅ Application started successfully!"
echo "🌐 Dashboard: http://localhost:8080"
echo "🔧 Health Check: http://localhost:8080/actuator/health"
echo "📊 Metrics: http://localhost:8080/actuator/metrics"
