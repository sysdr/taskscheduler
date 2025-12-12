#!/bin/bash
set -e

echo "Deploying to Kubernetes..."

# Check if kubectl can connect to a cluster
if ! kubectl cluster-info &>/dev/null; then
    echo "❌ Error: No Kubernetes cluster found or cluster is not accessible."
    echo ""
    echo "To set up a local cluster, you can use one of these options:"
    echo ""
    echo "Option 1: Use kind (Kubernetes in Docker)"
    echo "  curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.20.0/kind-linux-amd64"
    echo "  chmod +x ./kind"
    echo "  ./kind create cluster"
    echo ""
    echo "Option 2: Use minikube"
    echo "  minikube start"
    echo ""
    echo "Option 3: Use Docker Desktop Kubernetes"
    echo "  Enable Kubernetes in Docker Desktop settings"
    echo ""
    echo "After setting up a cluster, run this script again."
    exit 1
fi

# Apply configurations with validation disabled if needed
echo "Applying Kubernetes manifests..."
kubectl apply -f k8s/configmap.yaml --validate=false 2>/dev/null || kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/hpa.yaml

echo ""
echo "✅ Deployment complete!"
echo ""
echo "Check status:"
echo "  kubectl get pods"
echo "  kubectl get services"
echo "  kubectl get hpa"
echo ""
echo "View logs:"
echo "  kubectl logs -l app=task-scheduler -f"
