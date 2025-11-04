#!/bin/bash

set -e

echo "🎬 Task Scheduler Reliable Consumer Demo"
echo "======================================="

# Check if services are running
if ! curl -s http://localhost:8082/actuator/health > /dev/null; then
    echo "❌ Consumer service not running. Please run ./start.sh first"
    exit 1
fi

echo "✅ Consumer service is running"
echo "📊 Dashboard: http://localhost:8082"

# Check if Python kafka library is available
if ! python3 -c "import kafka" 2>/dev/null; then
    echo "⚠️ Installing kafka-python..."
    pip3 install kafka-python
fi

echo ""
echo "🎯 Running demo scenarios..."
echo ""

# Run test producer
echo "📤 Sending test tasks to demonstrate acknowledgment patterns..."
python3 test-producer.py

echo ""
echo "✅ Demo completed!"
echo ""
echo "📊 Check the dashboard at: http://localhost:8082"
echo "🔍 Monitor topics in Kafka UI: http://localhost:8080"
echo "📈 View metrics: http://localhost:8082/actuator/metrics"
echo ""
echo "🔧 What to observe:"
echo "   - Successful task processing"
echo "   - Retry attempts for transient failures"
echo "   - Dead letter queue for permanent failures"
echo "   - Real-time metrics updates"
