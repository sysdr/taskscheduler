#!/bin/bash
echo "🚀 Starting Multi-Tenant Task Scheduler..."

# Build first
./build.sh

if [ $? -ne 0 ]; then
    exit 1
fi

# Start application
java -jar target/*.jar

