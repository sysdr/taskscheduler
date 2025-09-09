#!/bin/bash

echo "🛑 Stopping Leader Election Scheduler instances..."

# Find and kill all instances
pkill -f "leader-election-scheduler"

echo "✅ All instances stopped"
