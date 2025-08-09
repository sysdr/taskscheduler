#!/bin/bash

set -e

# Resolve repository root so script works from any cwd
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$REPO_ROOT"

echo "🚀 Starting @Scheduled Duplication Demo"
echo "📊 This demo shows why @Scheduled is problematic in distributed environments"

# Function to check if a port is in use
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null; then
        echo "⚠️  Port $1 is already in use"
        return 1
    fi
    return 0
}

# Function to wait for application to start
wait_for_app() {
    local port=$1
    local instance_name=$2
    echo "⏳ Waiting for $instance_name to start on port $port..."
    
    for i in {1..30}; do
        if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
            echo "✅ $instance_name is running on port $port"
            return 0
        fi
        sleep 2
    done
    
    echo "❌ Failed to start $instance_name on port $port"
    return 1
}

# Ensure required directories exist before use
mkdir -p "$REPO_ROOT/logs" "$REPO_ROOT/scripts"

# Build the application
echo "🔨 Building application..."

# Prefer Maven Wrapper if executable, else fallback to system Maven
if [ -x "./mvnw" ] && head -n 1 ./mvnw | grep -q "^#!"; then
    MAVEN_CMD="./mvnw"
elif command -v mvn >/dev/null 2>&1; then
    MAVEN_CMD="mvn"
else
    echo "❌ Neither mvnw nor mvn found. Please install Maven or provide the Maven Wrapper."
    exit 1
fi

$MAVEN_CMD clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

echo "✅ Build successful"

# Pick a Java runtime that matches project target (from pom.xml)
JAVA_BIN="java"

version_ge() {
  # compare semantic-like numbers as strings via sort -V
  [ "$(printf '%s\n' "$1" "$2" | sort -V | head -n1)" = "$2" ]
}

detect_java_version() {
  "$1" -version 2>&1 | awk -F '"' '/version/ {print $2}' | sed 's/^\([0-9]\+\)\.\([0-9]\+\).*/\1/;s/^1\./1/'
}

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA_BIN="$JAVA_HOME/bin/java"
fi

JAVA_MAJOR="$(detect_java_version "$JAVA_BIN")"

if [ -z "$JAVA_MAJOR" ] || ! version_ge "$JAVA_MAJOR" "17"; then
  if command -v /usr/libexec/java_home >/dev/null 2>&1; then
    JAVA_CANDIDATE="$({ /usr/libexec/java_home -v 17+ 2>/dev/null || true; })"
    if [ -n "$JAVA_CANDIDATE" ] && [ -x "$JAVA_CANDIDATE/bin/java" ]; then
      JAVA_BIN="$JAVA_CANDIDATE/bin/java"
      JAVA_MAJOR="$(detect_java_version "$JAVA_BIN")"
    fi
  fi
fi

if [ -z "$JAVA_MAJOR" ] || ! version_ge "$JAVA_MAJOR" "17"; then
  echo "❌ A Java 17+ runtime is required to run the app. Current: ${JAVA_MAJOR:-unknown}."
  echo "   Tip (macOS): brew install temurin17 or set JAVA_HOME to a 17+ JDK."
  exit 1
fi
echo "☕ Using Java runtime: $JAVA_BIN (major $JAVA_MAJOR)"

# Check if Docker is requested
if [ "$1" = "docker" ]; then
    echo "🐳 Starting with Docker..."
    
    # Build and start Docker containers
    cd "$REPO_ROOT/docker"
    docker-compose down --remove-orphans
    docker-compose build
    docker-compose up -d
    
    echo "⏳ Waiting for Docker containers to start..."
    sleep 15
    
    # Check container health
    for port in 8081 8082 8083; do
        if wait_for_app $port "Docker Instance"; then
            echo "✅ Docker instance on port $port is ready"
        else
            echo "❌ Docker instance on port $port failed to start"
        fi
    done
    
    echo ""
    echo "🎉 Docker Demo is ready!"
    echo "📊 Access dashboards:"
    echo "   • Instance 1: http://localhost:8081"
    echo "   • Instance 2: http://localhost:8082" 
    echo "   • Instance 3: http://localhost:8083"
    echo ""
    echo "🔍 Watch Docker logs: docker-compose -f docker/docker-compose.yml logs -f"
    
else
    echo "💻 Starting with local Java processes..."
    
    # Check ports
    for port in 8080 8081 8082; do
        if ! check_port $port; then
            echo "Please free up port $port or kill existing processes"
            exit 1
        fi
    done
    
    # Start multiple instances
    echo "🚀 Starting Instance 1 on port 8080..."
    java -jar -Dserver.port=8080 -Dinstance.id=LOCAL-INSTANCE-1 target/scheduled-duplication-demo-1.0.0.jar > "$REPO_ROOT/logs/instance1.log" 2>&1 &
    INSTANCE1_PID=$!
    echo $INSTANCE1_PID > "$REPO_ROOT/scripts/instance1.pid"
    
    echo "🚀 Starting Instance 2 on port 8081..."
    java -jar -Dserver.port=8081 -Dinstance.id=LOCAL-INSTANCE-2 target/scheduled-duplication-demo-1.0.0.jar > "$REPO_ROOT/logs/instance2.log" 2>&1 &
    INSTANCE2_PID=$!
    echo $INSTANCE2_PID > "$REPO_ROOT/scripts/instance2.pid"
    
    echo "🚀 Starting Instance 3 on port 8082..."  
    java -jar -Dserver.port=8082 -Dinstance.id=LOCAL-INSTANCE-3 target/scheduled-duplication-demo-1.0.0.jar > "$REPO_ROOT/logs/instance3.log" 2>&1 &
    INSTANCE3_PID=$!
    echo $INSTANCE3_PID > "$REPO_ROOT/scripts/instance3.pid"
    
    # Wait for all instances to start
    wait_for_app 8080 "Instance 1" || exit 1
    wait_for_app 8081 "Instance 2" || exit 1  
    wait_for_app 8082 "Instance 3" || exit 1
    
    echo ""
    echo "🎉 Local Demo is ready!"
    echo "📊 Access dashboards:"
    echo "   • Instance 1: http://localhost:8080"
    echo "   • Instance 2: http://localhost:8081"
    echo "   • Instance 3: http://localhost:8082"
    echo ""
    echo "🔍 Watch logs:"
    echo "   • tail -f \"$REPO_ROOT/logs/instance1.log\""
    echo "   • tail -f \"$REPO_ROOT/logs/instance2.log\""
    echo "   • tail -f \"$REPO_ROOT/logs/instance3.log\""
fi

echo ""
echo "🚨 OBSERVE THE PROBLEM:"
echo "   • Each scheduled task runs on ALL instances simultaneously"
echo "   • Check the ERROR logs showing duplicate executions"
echo "   • Watch the dashboard counters showing multiple executions"
echo "   • This demonstrates why @Scheduled fails in distributed environments"
echo ""
echo "🛑 To stop: ./scripts/stop.sh"
