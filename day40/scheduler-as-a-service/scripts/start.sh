#!/bin/bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"
API_BASE="http://localhost:8080/api/v1"

mkdir -p "$LOG_DIR"

command -v jq >/dev/null 2>&1 || { echo "jq is required but not installed."; exit 1; }
command -v curl >/dev/null 2>&1 || { echo "curl is required but not installed."; exit 1; }

echo "Starting all services..."

ensure_artifacts() {
  local missing=0
  local services=("scheduler-service" "payment-service" "notification-service")
  for service in "${services[@]}"; do
    if [[ ! -f "$ROOT_DIR/$service/target/$service-1.0.0.jar" ]]; then
      missing=1
      break
    fi
  done

  if [[ $missing -eq 1 ]]; then
    echo "Building services (artifacts not found)..."
    "$ROOT_DIR/scripts/build.sh"
  fi
}

wait_for_scheduler() {
  echo "Waiting for Scheduler Service to start..."
  for _ in {1..30}; do
    if curl -sSf http://localhost:8080/actuator/health >/dev/null 2>&1; then
      echo "Scheduler Service is online."
      return
    fi
    sleep 2
  done
  echo "Scheduler Service failed to start in time. Check logs/scheduler.log"
  exit 1
}

register_tenant() {
  local name="$1"
  curl -sSf -X POST "$API_BASE/auth/register" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg tenantName "$name" '{tenantName: $tenantName}')"
}

fetch_token() {
  local api_key="$1"
  curl -sSf -X POST "$API_BASE/auth/token" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg apiKey "$api_key" '{apiKey: $apiKey}')" | jq -r '.token'
}

create_task() {
  local token="$1"
  local name="$2"
  local payload="$3"
  local priority="$4"
  local scheduled_for="${5:-}"
  local callback="${6:-}"

  local body
  body=$(jq -n \
    --arg taskName "$name" \
    --arg payload "$payload" \
    --arg priority "$priority" \
    --arg scheduled "$scheduled_for" \
    --arg callback "$callback" \
    '{
      taskName: $taskName,
      payload: $payload,
      priority: ($priority | tonumber),
      maxRetries: 3
    }
    + (if $scheduled != "" then {scheduledFor: $scheduled} else {} end)
    + (if $callback != "" then {callbackUrl: $callback} else {} end)')

  curl -sSf -X POST "$API_BASE/tasks" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $token" \
    -d "$body" >/dev/null
}

seed_demo_data() {
  local api_key="$1"
  local tenant_name="$2"
  echo "Seeding demo data for $tenant_name..."
  local token
  token=$(fetch_token "$api_key")

  create_task "$token" "Process subscription renewal" '{"customerId":"C-48291","plan":"PRO","amount":129.0}' 9
  create_task "$token" "Send payment reminder" '{"customerId":"C-19022","dueDate":"2025-11-20","amount":89.0}' 4
  local scheduled_time
  scheduled_time=$(date -d "+10 minutes" +"%Y-%m-%dT%H:%M:%S")
  create_task "$token" "Batch reconcile payouts" '{"batchId":"BATCH-778","records":42}' 6 "$scheduled_time"
}

write_demo_config() {
  local tenant_name="$1"
  local api_key="$2"
  cat > "$ROOT_DIR/dashboard/static/demo-config.json" <<EOF
{
  "tenantName": "$tenant_name",
  "apiKey": "$api_key",
  "lastUpdated": "$(date -Iseconds)"
}
EOF
}

start_service() {
  local service_dir="$1"
  local jar_name="$2"
  local log_name="$3"
  local pid_file="$4"
  shift 4

  (cd "$ROOT_DIR/$service_dir" && java -jar "$jar_name" "$@" > "$LOG_DIR/$log_name" 2>&1 & echo $! > "$ROOT_DIR/$pid_file")
}

ensure_artifacts

start_service "scheduler-service" "target/scheduler-service-1.0.0.jar" "scheduler.log" "scheduler.pid"
wait_for_scheduler

echo "Registering tenants..."
PAYMENT_RESPONSE=$(register_tenant "Payment Service")
NOTIFICATION_RESPONSE=$(register_tenant "Notification Service")

PAYMENT_API_KEY=$(echo "$PAYMENT_RESPONSE" | jq -r '.apiKey')
NOTIFICATION_API_KEY=$(echo "$NOTIFICATION_RESPONSE" | jq -r '.apiKey')

if [[ -z "$PAYMENT_API_KEY" || -z "$NOTIFICATION_API_KEY" ]]; then
  echo "Failed to retrieve API keys. Aborting."
  exit 1
fi

seed_demo_data "$PAYMENT_API_KEY" "Payment Service"
write_demo_config "Payment Service" "$PAYMENT_API_KEY"

echo "Starting Payment Service..."
start_service "payment-service" "target/payment-service-1.0.0.jar" "payment.log" "payment.pid" \
  --server.port=8081 \
  --spring.application.name=payment-service \
  --scheduler.url=http://localhost:8080 \
  --scheduler.api-key="$PAYMENT_API_KEY"

echo "Starting Notification Service..."
start_service "notification-service" "target/notification-service-1.0.0.jar" "notification.log" "notification.pid" \
  --server.port=8082 \
  --spring.application.name=notification-service \
  --scheduler.url=http://localhost:8080 \
  --scheduler.api-key="$NOTIFICATION_API_KEY"

echo "Starting Dashboard..."
(cd "$ROOT_DIR" && python3 dashboard/serve_dashboard.py > "$LOG_DIR/dashboard.log" 2>&1 & echo $! > "$ROOT_DIR/dashboard.pid")

echo ""
echo "==================================="
echo "All services started successfully!"
echo "==================================="
echo "Scheduler Service: http://localhost:8080"
echo "Payment Service: http://localhost:8081"
echo "Notification Service: http://localhost:8082"
echo "Dashboard: http://localhost:8083"
echo ""
echo "Payment API Key: $PAYMENT_API_KEY"
echo "Notification API Key: $NOTIFICATION_API_KEY"
