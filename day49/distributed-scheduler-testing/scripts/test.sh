#!/bin/bash
cd "$(dirname "$0")/.."
docker run -d --name redis-test -p 6379:6379 redis:7-alpine
sleep 2
mvn test
docker stop redis-test && docker rm redis-test
