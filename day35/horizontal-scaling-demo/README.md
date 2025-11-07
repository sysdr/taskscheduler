# Day 35: Horizontal Scaling Demo

## Project Structure
```
horizontal-scaling-demo/
├── producer/          # Task producer service
├── consumer/          # Task consumer service
├── dashboard/         # Monitoring dashboard
├── docker/           # Docker Compose configuration
├── build.sh          # Build all services
├── start.sh          # Start without Docker
├── stop.sh           # Stop all services
├── start-docker.sh   # Start with Docker
└── stop-docker.sh    # Stop Docker services
```

## Quick Start (Docker)
```bash
./build.sh
./start-docker.sh
```

## Quick Start (Without Docker)
1. Start RabbitMQ: `docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management`
2. Start Redis: `docker run -d -p 6379:6379 redis:7-alpine`
3. Build: `./build.sh`
4. Start: `./start.sh`

## Access Points
- Dashboard: http://localhost:8082
- Producer API: http://localhost:8080
- RabbitMQ Management: http://localhost:15672 (guest/guest)

## Testing Scaling
1. Open dashboard
2. Produce 100 tasks (observe processing with 3 consumers)
3. Stop one consumer: `docker stop consumer-2`
4. Watch remaining consumers pick up the load
5. Restart consumer: `docker start consumer-2`
6. Scale to 5 consumers: Edit docker-compose.yml and add consumer-4, consumer-5

## Verification
- All consumers should show roughly equal task counts
- Throughput should scale linearly with consumer count
- Stopping a consumer should not lose any tasks
