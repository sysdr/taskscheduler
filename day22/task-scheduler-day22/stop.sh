#!/bin/bash
echo "🛑 Stopping Task Scheduler Application..."

# Kill any Java processes running the task scheduler
pkill -f "task-scheduler-day22" 2>/dev/null || true
pkill -f "spring-boot:run" 2>/dev/null || true

# More specific kill for Spring Boot applications
ps aux | grep "task-scheduler-day22" | grep -v grep | awk '{print $2}' | xargs kill -9 2>/dev/null || true

echo "Application stopped"
