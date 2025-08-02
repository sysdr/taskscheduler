#!/bin/bash

echo "🚀 Starting TaskScheduler Pro - Enterprise Dashboard"
echo "=================================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or later."
    exit 1
fi

echo "🔧 Installing dependencies and building..."
mvn clean install -DskipTests

echo "🧪 Running tests..."
mvn test

echo "🚀 Starting application..."
nohup mvn spring-boot:run > app.log 2>&1 &
APP_PID=$!
echo $APP_PID > app.pid

echo "⏳ Waiting for application to start..."
sleep 15

# Check if application is running
if curl -f http://localhost:8080/actuator/health &> /dev/null; then
    echo "✅ Application started successfully!"
    echo ""
    echo "🎯 Enterprise Dashboard Features:"
    echo "   • Professional dark sidebar navigation"
    echo "   • Real-time metric cards with trend indicators"
    echo "   • Interactive charts and analytics"
    echo "   • Mobile-responsive design"
    echo "   • Keyboard shortcuts (Ctrl/Cmd + K for search)"
    echo "   • Professional data tables with status badges"
    echo ""
    echo "🌐 Access Points:"
    echo "📊 Dashboard: http://localhost:8080"
    echo "📈 Health Check: http://localhost:8080/actuator/health"
    echo "📋 Scheduled Tasks: http://localhost:8080/actuator/scheduledtasks"
    echo ""
    echo "🔧 Management Commands:"
    echo "🔍 Watch logs: tail -f app.log"
    echo "🛑 Stop application: ./stop.sh"
    echo "🔄 Restart application: ./stop.sh && ./start.sh"
    echo ""
    echo "💡 Tips:"
    echo "   • Use Ctrl/Cmd + K to open search modal"
    echo "   • Press Esc to close modals/sidebar"
    echo "   • Dashboard auto-refreshes every 10 seconds"
    echo "   • Mobile-friendly: sidebar collapses on small screens"
    echo ""
    echo "🎉 Enjoy your professional task scheduler dashboard!"
else
    echo "❌ Application failed to start. Check app.log for details."
    echo "🔍 Debug commands:"
    echo "   tail -f app.log"
    echo "   ps aux | grep java"
    echo "   lsof -i :8080"
    exit 1
fi
