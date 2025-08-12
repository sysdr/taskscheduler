#!/bin/bash

set -e

# Resolve repository root so script works from any cwd
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$REPO_ROOT"

CLEAN_DOCKER=0
for arg in "$@"; do
  case "$arg" in
    --docker|docker)
      CLEAN_DOCKER=1
      ;;
  esac
done

echo "🧹 Cleaning project at: $REPO_ROOT"

if [ $CLEAN_DOCKER -eq 1 ]; then
  echo "🛑 Stopping app and Docker containers..."
  "$REPO_ROOT/scripts/stop.sh" docker || true
else
  echo "🛑 Stopping app processes..."
  "$REPO_ROOT/scripts/stop.sh" || true
fi

echo "🧽 Removing PID files..."
rm -f "$REPO_ROOT"/scripts/instance*.pid || true

echo "🧽 Removing logs..."
rm -rf "$REPO_ROOT/logs" || true

echo "🧽 Cleaning build artifacts (mvn clean)..."
if [ -x "$REPO_ROOT/mvnw" ] && head -n 1 "$REPO_ROOT/mvnw" | grep -q "^#!"; then
  "$REPO_ROOT/mvnw" clean -q || true
elif command -v mvn >/dev/null 2>&1; then
  mvn clean -q || true
fi

echo "🧽 Removing target directories..."
rm -rf "$REPO_ROOT/target" || true

if [ $CLEAN_DOCKER -eq 1 ]; then
  if command -v docker-compose >/dev/null 2>&1; then
    echo "🐳 Cleaning Docker artifacts (images, volumes)..."
    (cd "$REPO_ROOT/docker" && docker-compose down --remove-orphans -v --rmi local || true)
    docker image prune -f >/dev/null 2>&1 || true
    docker volume prune -f >/dev/null 2>&1 || true
  fi
fi

echo "✅ Cleanup complete."

