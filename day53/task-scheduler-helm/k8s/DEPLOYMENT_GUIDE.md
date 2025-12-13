# Kubernetes Deployment Guide

## Prerequisites
- Kubernetes cluster (Minikube, Kind, or cloud provider)
- Helm 3.x installed
- kubectl configured

## Installation Steps

### 1. Add Bitnami Repository
```bash
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
```

### 2. Install Dependencies
```bash
cd helm-chart/task-scheduler
helm dependency update
```

### 3. Install to Development
```bash
helm install task-scheduler . --namespace task-scheduler --create-namespace
```

### 4. Install to Production
```bash
helm install task-scheduler . -f values-production.yaml --namespace production --create-namespace
```

## Verification

### Check Deployment Status
```bash
kubectl get pods -n task-scheduler
kubectl get svc -n task-scheduler
```

### Check Helm Release
```bash
helm list -n task-scheduler
helm status task-scheduler -n task-scheduler
```

### Access Application
```bash
kubectl port-forward svc/task-scheduler 8080:8080 -n task-scheduler
```

## Operations

### Upgrade Deployment
```bash
helm upgrade task-scheduler . -n task-scheduler
```

### Rollback
```bash
helm rollback task-scheduler -n task-scheduler
```

### Uninstall
```bash
helm uninstall task-scheduler -n task-scheduler
```

## Monitoring

### View Logs
```bash
kubectl logs -f deployment/task-scheduler -n task-scheduler
```

### Check Health
```bash
kubectl exec -it deployment/task-scheduler -n task-scheduler -- wget -qO- localhost:8080/actuator/health
```

## Scaling

### Manual Scaling
```bash
kubectl scale deployment task-scheduler --replicas=5 -n task-scheduler
```

### Check HPA
```bash
kubectl get hpa -n task-scheduler
```

## Troubleshooting

### Debug Pod Issues
```bash
kubectl describe pod <pod-name> -n task-scheduler
kubectl logs <pod-name> -n task-scheduler
```

### Access Pod Shell
```bash
kubectl exec -it <pod-name> -n task-scheduler -- /bin/sh
```
