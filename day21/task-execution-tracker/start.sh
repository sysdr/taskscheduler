#!/bin/bash

echo "🚀 Starting Task Execution Tracker..."

# Start the application using Maven
mvn spring-boot:run -DskipTests &

APP_PID=$!
echo "Application started with PID: $APP_PID"
echo $APP_PID > app.pid

echo "✅ Application is running at http://localhost:8080"
echo "📊 Dashboard: http://localhost:8080/"
echo "🗄️  H2 Console: http://localhost:8080/h2-console"
echo "📊 Actuator: http://localhost:8080/actuator"

wait $APP_PID
