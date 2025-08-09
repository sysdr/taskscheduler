#!/bin/bash
set -euo pipefail

echo "🚀 Starting Distributed Task Scheduler Demo"

# Check if Redis is running
if ! pgrep -x "redis-server" > /dev/null 2>&1; then
  echo "⚠️  Redis not found. Please install and start Redis:"
  echo "   - macOS: brew install redis && brew services start redis"
  echo "   - Ubuntu: sudo apt install redis-server && sudo systemctl start redis"
  echo "   - Docker: docker run -d -p 6379:6379 redis:alpine"
fi

# Stop an existing instance if running
if [[ -f app.pid ]]; then
  if ps -p "$(cat app.pid)" > /dev/null 2>&1; then
    echo "⏹️  Existing instance found (PID $(cat app.pid)). Stopping..."
    ./stop.sh || true
    sleep 1
  else
    rm -f app.pid || true
  fi
fi

# Resolve Java runtime (prefer Homebrew OpenJDK if available)
JAVA_BIN="java"
if command -v /opt/homebrew/opt/openjdk/bin/java >/dev/null 2>&1; then
  JAVA_BIN="/opt/homebrew/opt/openjdk/bin/java"
fi
echo "💡 Using Java at: ${JAVA_BIN} ($(${JAVA_BIN} -version 2>&1 | head -n1))"

# Build the application
echo "🔨 Building application..."
if [[ -x ./mvnw ]]; then
  ./mvnw -q -DskipTests clean package
else
  mvn -q -DskipTests clean package
fi

# Locate the built jar (exclude .original)
JAR_FILE=$(ls -1 target/*.jar 2>/dev/null | grep -v ".original$" | head -n 1 || true)
if [[ -z "${JAR_FILE}" ]]; then
  echo "❌ Build did not produce a runnable jar in target/. Aborting."
  exit 1
fi
echo "📦 Using artifact: ${JAR_FILE}"

# Start the application
echo "▶️  Starting scheduler instance..."
export SPRING_PROFILES_ACTIVE="default"
nohup "${JAVA_BIN}" -jar "${JAR_FILE}" > app.out 2>&1 &
APP_PID=$!
echo ${APP_PID} > app.pid

# Wait for app to come up (health endpoint or port)
START_TIMEOUT=30
APP_UP=0
for i in $(seq 1 ${START_TIMEOUT}); do
  if command -v curl >/dev/null 2>&1 && curl -sf "http://localhost:8080/actuator/health" >/dev/null 2>&1; then
    APP_UP=1; break
  fi
  if lsof -iTCP:8080 -sTCP:LISTEN >/dev/null 2>&1; then
    APP_UP=1; break
  fi
  sleep 1
done

if [[ ${APP_UP} -eq 1 ]]; then
  echo "✅ Application started with PID: ${APP_PID}"
  echo "📊 Dashboard: http://localhost:8080/scheduler/dashboard"
  echo "🔍 H2 Console: http://localhost:8080/h2-console"
  echo "📈 Actuator: http://localhost:8080/actuator/health"
else
  echo "⚠️  Application may not have started yet. Check app.out for details. PID: ${APP_PID}"
fi

echo "📝 Logs: tail -f app.out"
echo "💤 Running in background. To stop: ./stop.sh"
