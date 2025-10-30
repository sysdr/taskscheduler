#!/bin/bash

echo "🎭 Running Message Queue Lab Demo..."

# Send test messages to Kafka
echo "📤 Sending test messages to Kafka..."
curl -X POST http://localhost:8090/api/kafka/send \
  -d "topic=task-submission" \
  -d "taskType=EMAIL_NOTIFICATION" \
  -d "payload=Send welcome email to new user"

curl -X POST http://localhost:8090/api/kafka/send \
  -d "topic=task-execution" \
  -d "taskType=DATA_PROCESSING" \
  -d "payload=Process user analytics data"

# Send test messages to RabbitMQ
echo "📤 Sending test messages to RabbitMQ..."
curl -X POST http://localhost:8090/api/rabbitmq/send \
  -d "routingKey=task.submission" \
  -d "taskType=REPORT_GENERATION" \
  -d "payload=Generate monthly sales report"

curl -X POST http://localhost:8090/api/rabbitmq/send \
  -d "routingKey=task.execution" \
  -d "taskType=FILE_CLEANUP" \
  -d "payload=Clean up temporary files older than 7 days"

echo "✅ Demo messages sent!"
echo "📊 Check the dashboard at http://localhost:8090"
echo "🔧 Monitor Kafka at http://localhost:8080"
echo "🐰 Monitor RabbitMQ at http://localhost:15672"
