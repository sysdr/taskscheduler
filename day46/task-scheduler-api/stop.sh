#!/bin/bash

echo "Stopping Task Scheduler API..."

if [ -f app.pid ]; then
    kill $(cat app.pid)
    rm app.pid
    echo "✅ Application stopped!"
else
    docker-compose down
    echo "✅ Docker containers stopped!"
fi
