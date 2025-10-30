# Message Queue Lab - Day 31

## Overview
This project demonstrates the fundamental concepts of message queues using Apache Kafka and RabbitMQ in a Spring Boot application.

## Features
- Apache Kafka integration with multiple topics
- RabbitMQ integration with exchanges and routing
- Modern web dashboard for message management
- Docker-based setup for easy deployment
- Producer and consumer implementations
- Real-time message monitoring

## Quick Start

### Prerequisites
- Java 21
- Maven 3.6+
- Docker & Docker Compose

### Setup & Run
```bash
# Build the application
./scripts/build.sh

# Start all services
./scripts/start.sh

# Run demo (optional)
./scripts/demo.sh

# Stop all services
./scripts/stop.sh
```

### Access Points
- **Dashboard**: http://localhost:8090
- **Kafka UI**: http://localhost:8080
- **RabbitMQ Management**: http://localhost:15672 (admin/admin123)

## Architecture
- **Kafka**: High-throughput message streaming
- **RabbitMQ**: Reliable message queuing with routing
- **Spring Boot**: Application framework with message broker integration
- **Docker**: Containerized broker deployment

## Learning Objectives
- Understand message queue concepts
- Compare Kafka vs RabbitMQ
- Implement producers and consumers
- Experience decoupled system architecture
- Monitor message flow and performance
