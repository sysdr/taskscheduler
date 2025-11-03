#!/bin/bash

echo "🎭 Task Consumer Service Demo"

# Check if application is running
if ! curl -s http://localhost:8080/api/stats > /dev/null; then
    echo "❌ Application not running. Please start it first with ./start.sh"
    exit 1
fi

echo "🌟 Demo: Task Consumer Service in Action"
echo "========================================"

# Install jq if not present for JSON parsing
if ! command -v jq &> /dev/null; then
    echo "📦 Installing jq for JSON processing..."
    if command -v apt-get &> /dev/null; then
        sudo apt-get update && sudo apt-get install -y jq
    elif command -v brew &> /dev/null; then
        brew install jq
    else
        echo "⚠️ Please install jq manually for better demo experience"
    fi
fi

# Function to send a test task
send_task() {
    local task_id=$1
    local task_type=$2
    local payload=$3
    
    echo "📤 Sending $task_type task: $task_id"
    
    # Here we would typically send to RabbitMQ, but for demo we'll simulate
    # In a real scenario, this would be done by the scheduler service
    echo "   Task details: $payload"
}

# Demo scenario
echo "🎬 Starting demo scenario..."
echo ""

echo "1. 📊 Current system stats:"
curl -s http://localhost:8080/api/stats | jq . || echo "Getting stats..."
echo ""

echo "2. 📧 Simulating task processing..."
send_task "demo-email-001" "email" '{"to":"user@example.com","subject":"Welcome"}'
send_task "demo-report-002" "report" '{"type":"monthly","format":"pdf"}'
send_task "demo-backup-003" "backup" '{"database":"users","incremental":true}'

echo ""
echo "3. 🌐 Access points:"
echo "   • Dashboard: http://localhost:8080/"
echo "   • API Stats: http://localhost:8080/api/stats"
echo "   • H2 Console: http://localhost:8080/h2-console"
echo "   • Health Check: http://localhost:8080/actuator/health"
echo "   • RabbitMQ Management: http://localhost:15672/ (guest/guest)"

echo ""
echo "4. 🎯 Key features demonstrated:"
echo "   ✓ Message queue integration with RabbitMQ"
echo "   ✓ Asynchronous task processing"
echo "   ✓ Real-time dashboard with modern UI"
echo "   ✓ Multiple task types support"
echo "   ✓ Task state management"
echo "   ✓ Worker identification and tracking"
echo "   ✓ REST API for monitoring"
echo "   ✓ Docker containerization"

echo ""
echo "🎉 Demo completed! Open http://localhost:8080/ to see the dashboard"
echo "💡 Pro tip: Start multiple instances on different ports to see competing consumers in action!"
