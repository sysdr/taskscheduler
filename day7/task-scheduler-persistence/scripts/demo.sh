#!/bin/bash

echo "🎭 Task Scheduler Persistence Demo"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

BASE_URL="http://localhost:8080"

# Function to check if app is running
check_app() {
    curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1
    return $?
}

# Function to cleanup on exit
cleanup() {
    echo -e "\n${YELLOW}Cleaning up...${NC}"
    if [ ! -z "$TASK_ID" ]; then
        echo -e "${YELLOW}Deleting demo task $TASK_ID...${NC}"
        curl -s -X DELETE "$BASE_URL/api/tasks/$TASK_ID" > /dev/null 2>&1
        echo -e "${GREEN}Demo task deleted successfully${NC}"
    fi
    echo -e "${GREEN}Cleanup completed${NC}"
}

# Set trap to cleanup on script exit
trap cleanup EXIT

echo -e "${BLUE}Waiting for application to be ready...${NC}"
sleep 5

# Wait for app to be ready
for i in {1..30}; do
    if check_app; then
        echo -e "${GREEN}Application is ready!${NC}"
        break
    fi
    echo -e "${YELLOW}Waiting for application... ($i/30)${NC}"
    sleep 2
done

if ! check_app; then
    echo -e "${RED}Application failed to start properly${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}=== Task Scheduler Persistence Demo ===${NC}"
echo ""

# 1. Get initial task statistics
echo -e "${YELLOW}1. Getting initial task statistics...${NC}"
curl -s "$BASE_URL/api/tasks/stats" | jq . 2>/dev/null || echo "Failed to get statistics"
echo ""

# 2. List all tasks
echo -e "${YELLOW}2. Listing all existing tasks...${NC}"
curl -s "$BASE_URL/api/tasks?size=20" | jq '.content[] | {id, name, type, status, enabled}' 2>/dev/null || echo "Failed to list tasks"
echo ""

# 3. Create a new task
echo -e "${YELLOW}3. Creating a new task...${NC}"
NEW_TASK=$(cat << 'JSON'
{
    "name": "demo-email-task",
    "type": "EMAIL",
    "scheduleExpression": "0 0 9 * * MON-FRI",
    "description": "Send daily email notifications to users",
    "payload": "{\"template\": \"daily-summary\", \"recipients\": [\"admin@company.com\"]}",
    "status": "ACTIVE",
    "enabled": true,
    "maxRetries": 5
}
JSON
)

CREATED_TASK=$(curl -s -X POST "$BASE_URL/api/tasks" \
    -H "Content-Type: application/json" \
    -d "$NEW_TASK")
echo "$CREATED_TASK" | jq . 2>/dev/null || echo "Failed to create task"
TASK_ID=$(echo "$CREATED_TASK" | jq -r '.id' 2>/dev/null)
echo ""

if [ -z "$TASK_ID" ] || [ "$TASK_ID" = "null" ]; then
    echo -e "${RED}Failed to create task or get task ID${NC}"
    exit 1
fi

# 4. Get the created task by ID
echo -e "${YELLOW}4. Retrieving the created task by ID ($TASK_ID)...${NC}"
curl -s "$BASE_URL/api/tasks/$TASK_ID" | jq . 2>/dev/null || echo "Failed to retrieve task"
echo ""

# 5. Update the task
echo -e "${YELLOW}5. Updating the task description...${NC}"
UPDATE_TASK=$(cat << JSON
{
    "name": "demo-email-task",
    "type": "EMAIL",
    "scheduleExpression": "0 0 9 * * MON-FRI",
    "description": "Updated: Send daily email notifications with new template",
    "payload": "{\"template\": \"daily-summary-v2\", \"recipients\": [\"admin@company.com\", \"manager@company.com\"]}",
    "status": "ACTIVE",
    "enabled": true,
    "maxRetries": 3
}
JSON
)

curl -s -X PUT "$BASE_URL/api/tasks/$TASK_ID" \
    -H "Content-Type: application/json" \
    -d "$UPDATE_TASK" | jq . 2>/dev/null || echo "Failed to update task"
echo ""

# 6. Get tasks by type
echo -e "${YELLOW}6. Getting all EMAIL type tasks...${NC}"
curl -s "$BASE_URL/api/tasks/type/EMAIL" | jq '.[] | {id, name, description, status}' 2>/dev/null || echo "Failed to get tasks by type"
echo ""

# 7. Get active tasks
echo -e "${YELLOW}7. Getting all active tasks...${NC}"
curl -s "$BASE_URL/api/tasks/active" | jq '.[] | {id, name, type, status}' 2>/dev/null || echo "Failed to get active tasks"
echo ""

# 8. Deactivate the task
echo -e "${YELLOW}8. Deactivating the demo task...${NC}"
curl -s -X PUT "$BASE_URL/api/tasks/$TASK_ID/deactivate" | jq . 2>/dev/null || echo "Failed to deactivate task"
echo ""

# 9. Get updated statistics
echo -e "${YELLOW}9. Getting updated task statistics...${NC}"
curl -s "$BASE_URL/api/tasks/stats" | jq . 2>/dev/null || echo "Failed to get updated statistics"
echo ""

# 10. Clean up - delete the demo task
echo -e "${YELLOW}10. Cleaning up - deleting the demo task...${NC}"
curl -s -X DELETE "$BASE_URL/api/tasks/$TASK_ID" > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Demo task deleted successfully${NC}"
else
    echo -e "${RED}Failed to delete demo task${NC}"
fi
echo ""

echo -e "${GREEN}=== Demo completed successfully! ===${NC}"
echo ""
echo -e "${BLUE}Available endpoints:${NC}"
echo "  Web Dashboard: http://localhost:8080"
echo "  H2 Console: http://localhost:8080/h2-console (dev mode)"
echo "  Adminer: http://localhost:8081 (prod mode)"
echo "  API Documentation: http://localhost:8080/api/tasks"
echo "  Health Check: http://localhost:8080/actuator/health"
echo ""

# Clear the trap since we're exiting normally
trap - EXIT
