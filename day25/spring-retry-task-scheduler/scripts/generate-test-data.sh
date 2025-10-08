#!/bin/bash

echo "🚀 Generating test data for monitoring..."

# Create multiple tasks to generate metrics
echo "Creating test tasks..."

# Create EMAIL_NOTIFICATION tasks
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"name": "Welcome Email", "description": "Send welcome email", "type": "EMAIL_NOTIFICATION", "maxRetries": 3}' > /dev/null

curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"name": "Password Reset", "description": "Send password reset email", "type": "EMAIL_NOTIFICATION", "maxRetries": 2}' > /dev/null

# Create DATABASE_CLEANUP tasks
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"name": "Clean Old Logs", "description": "Clean up old log files", "type": "DATABASE_CLEANUP", "maxRetries": 5}' > /dev/null

# Create API_SYNC tasks
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"name": "Sync User Data", "description": "Sync user data with external API", "type": "API_SYNC", "maxRetries": 4}' > /dev/null

# Create FILE_PROCESSING tasks
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"name": "Process Upload", "description": "Process uploaded file", "type": "FILE_PROCESSING", "maxRetries": 3}' > /dev/null

echo "✅ Test data created!"
echo "📊 Check Prometheus at: http://localhost:9090"
echo "📈 Check Grafana at: http://localhost:3000 (admin/admin)"
echo ""
echo "Generated tasks:"
curl -s http://localhost:8080/api/tasks | jq '.[] | {id, name, type, status}' 2>/dev/null || curl -s http://localhost:8080/api/tasks
