#!/bin/bash
echo "🛑 Stopping Multi-Tenant Task Scheduler..."

pkill -f "multitenant-scheduler"

echo "✅ Stopped!"
