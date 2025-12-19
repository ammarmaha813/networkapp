#!/bin/bash
set -e

# Usage: ./scale.sh <namespace> <deployment> <replicas>

NAMESPACE="${1:-security-dev}"
DEPLOYMENT="${2:-network-security-dev-network-security-platform}"
REPLICAS="${3:-3}"

echo "📏 Scaling du déploiement"
echo "📦 Namespace: $NAMESPACE"
echo "🚀 Deployment: $DEPLOYMENT"
echo "🔢 Nouveau nombre de replicas: $REPLICAS"

# Vérifier si le deployment existe
if ! kubectl get deployment $DEPLOYMENT -n $NAMESPACE >/dev/null 2>&1; then
    echo "❌ Deployment $DEPLOYMENT non trouvé dans le namespace $NAMESPACE"
    exit 1
fi

# Scaler
echo "⏳ Scaling en cours..."
kubectl scale deployment $DEPLOYMENT --replicas=$REPLICAS -n $NAMESPACE

# Attendre que le scaling soit terminé
echo "⏰ Attente du scaling..."
kubectl rollout status deployment/$DEPLOYMENT -n $NAMESPACE --timeout=300s

echo "✅ Scaling terminé avec succès!"
kubectl get deployment $DEPLOYMENT -n $NAMESPACE