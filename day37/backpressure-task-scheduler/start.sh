#!/bin/bash
echo "Starting Backpressure Task Scheduler..."

# Start Kafka
cd docker
docker-compose up -d
cd ..

# Wait for Kafka
echo "Waiting for Kafka to be ready..."
sleep 10

# Start application
PORT_INPUT="${SERVER_PORT:-${1:-8080}}"

is_port_available() {
  ! ss -ltn "( sport = :$1 )" | grep -q ":$1"
}

PORT="${PORT_INPUT}"
while ! is_port_available "${PORT}"; do
  NEXT=$((PORT + 1))
  echo "Port ${PORT} is already in use. Attempting ${NEXT}..."
  PORT="${NEXT}"
done

echo "Starting application on port ${PORT}..."
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT}"
