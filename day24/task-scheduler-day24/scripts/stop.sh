#!/bin/bash

echo "🛑 Stopping Task Scheduler Day 24..."

# Find and kill Java processes
pkill -f "task-scheduler-day24-1.0.0.jar"

echo "✅ Application stopped!"
