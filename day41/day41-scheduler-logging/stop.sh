#!/bin/bash
echo "Stopping Scheduler Logging Application..."
pkill -f "day41-scheduler-logging" || echo "No process found"
