#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_PATH="$SCRIPT_DIR/target/task-scheduler-ui-1.0.0.jar"

echo "Starting Task Scheduler UI..."

if [[ ! -f "$JAR_PATH" ]]; then
  echo "Build artifact missing. Running build script..."
  bash "$SCRIPT_DIR/build.sh"
fi

if [[ ! -f "$JAR_PATH" ]]; then
  echo "Error: Expected JAR not found at $JAR_PATH" >&2
  exit 1
fi

java -jar "$JAR_PATH"
