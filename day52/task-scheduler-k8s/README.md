# Task Scheduler - Kubernetes and Containerization

## Overview
Production-ready task scheduler with Docker containerization and Kubernetes orchestration.

## Quick Start

### With Docker Compose
```bash
./build.sh
./start.sh
```

Access: http://localhost:8080

### With Kubernetes
```bash
./build.sh
./k8s-deploy.sh
```

## Features
- Multi-stage Docker build with layer caching
- Kubernetes deployment with 3 replicas
- Horizontal Pod Autoscaling
- Health checks (liveness & readiness)
- Resource limits and requests
- Prometheus metrics
- Modern web dashboard

## Project Structure
```
task-scheduler-k8s/
├── src/                    # Spring Boot application
├── k8s/                    # Kubernetes manifests
├── docker/                 # Docker configurations
├── Dockerfile              # Multi-stage build
├── docker-compose.yml      # Local deployment
└── scripts                 # Build and deployment scripts
```

## Kubernetes Resources
- **Deployment**: 3 replicas with rolling updates
- **Service**: LoadBalancer for external access
- **HPA**: Auto-scaling based on CPU/memory
- **ConfigMap**: External configuration

## Testing
```bash
# Check pod status
kubectl get pods

# View logs
kubectl logs -l app=task-scheduler -f

# Test autoscaling
kubectl get hpa

# Port forward
kubectl port-forward svc/task-scheduler-service 8080:80
```

## Docker Optimization
- Multi-stage build reduces image size
- Layer caching speeds up builds
- Spring Boot layered JARs
- Non-root user for security
- Health checks for reliability

## Cleanup
```bash
# Docker Compose
./stop.sh

# Kubernetes
kubectl delete -f k8s/
```
