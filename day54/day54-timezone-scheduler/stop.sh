#!/bin/bash
echo "Stopping application..."
pkill -f "timezone-scheduler" || true
docker-compose down 2>/dev/null || true
echo "✓ Application stopped"
