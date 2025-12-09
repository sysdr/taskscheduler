#!/bin/bash
cd "$(dirname "$0")/.."
docker run -d --name redis-sched -p 6379:6379 redis:7-alpine 2>/dev/null || true
mvn spring-boot:run
