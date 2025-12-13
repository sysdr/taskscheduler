# Day 53: Helm Charts for Kubernetes Deployment

## Overview
Production-ready Helm chart for deploying the Task Scheduler to Kubernetes with Redis and PostgreSQL dependencies.

## Features
- ⎈ Kubernetes-native deployment
- 📦 Helm chart packaging
- 🔄 Horizontal Pod Autoscaling
- 🏥 Health checks (liveness & readiness)
- 🛡️ Pod Disruption Budget
- 🔐 ConfigMaps & Secrets management
- 📊 Prometheus metrics integration
- 🔗 Service dependencies (Redis, PostgreSQL)

## Quick Start

### Local Development (Docker Compose)
```bash
# Build and start
./build.sh
./start.sh

# Access dashboard
open http://localhost:8080

# Stop
./stop.sh
```

### Kubernetes Deployment

#### Using Minikube
```bash
# Start Minikube
minikube start

# Build image
./build.sh

# Load image to Minikube
minikube image load task-scheduler:1.0.0

# Add Helm repos
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Install chart
cd helm-chart/task-scheduler
helm dependency update
helm install task-scheduler . --create-namespace --namespace task-scheduler

# Port forward
kubectl port-forward svc/task-scheduler 8080:8080 -n task-scheduler
```

## Project Structure
```
task-scheduler-helm/
├── src/main/java/com/taskscheduler/   # Spring Boot application
├── helm-chart/task-scheduler/         # Helm chart
│   ├── Chart.yaml                     # Chart metadata
│   ├── values.yaml                    # Default values
│   ├── values-production.yaml         # Production overrides
│   ├── templates/                     # Kubernetes manifests
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   ├── hpa.yaml
│   │   └── pdb.yaml
│   └── charts/                        # Dependencies
├── k8s/                               # Kubernetes guides
├── Dockerfile                         # Container image
├── docker-compose.yml                 # Local development
└── build.sh, start.sh, stop.sh       # Helper scripts
```

## Helm Chart Configuration

### Key Values
- `replicaCount`: Number of pod replicas (default: 2)
- `image.tag`: Docker image tag
- `resources`: CPU/memory limits
- `autoscaling`: HPA configuration
- `redis.enabled`: Enable Redis dependency
- `postgresql.enabled`: Enable PostgreSQL dependency

### Deployment Environments

#### Development
```bash
helm install task-scheduler . --set replicaCount=1
```

#### Production
```bash
helm install task-scheduler . -f values-production.yaml
```

## API Endpoints
- `GET /api/tasks` - List all tasks
- `POST /api/tasks` - Create task
- `GET /api/tasks/{id}` - Get task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `GET /api/tasks/stats` - Statistics

## Monitoring
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/prometheus
- Info: http://localhost:8080/actuator/info

## Assignment
Extend the chart for multi-region deployment with region-specific configuration.

## Next Lesson
Day 54: Handling Time Zones and Daylight Saving in Schedules
