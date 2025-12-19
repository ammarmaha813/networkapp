#!/bin/bash
set -e

# Configuration
NAMESPACE="${1:-security-dev}"
RELEASE_NAME="${2:-network-security-dev}"

echo "🔍 Vérification de la santé du déploiement"
echo "📦 Namespace: $NAMESPACE"
echo "🔧 Release: $RELEASE_NAME"
echo ""

# Fonction pour afficher en couleur
green() { echo -e "\033[32m$1\033[0m"; }
red() { echo -e "\033[31m$1\033[0m"; }
yellow() { echo -e "\033[33m$1\033[0m"; }

# Vérifier si le namespace existe
if ! kubectl get namespace $NAMESPACE >/dev/null 2>&1; then
    red "❌ Le namespace $NAMESPACE n'existe pas"
    exit 1
fi

echo "📋 1. Statut des pods:"
PODS_STATUS=$(kubectl get pods -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform -o jsonpath='{.items[*].status.phase}')
if echo "$PODS_STATUS" | grep -q "Running"; then
    green "✅ Des pods sont en cours d'exécution"
else
    red "❌ Aucun pod en cours d'exécution"
fi
kubectl get pods -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform --show-labels
echo ""

echo "🚀 2. Statut du deployment:"
DEPLOYMENT_READY=$(kubectl get deployment -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform -o jsonpath='{.items[*].status.readyReplicas}')
DEPLOYMENT_DESIRED=$(kubectl get deployment -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform -o jsonpath='{.items[*].spec.replicas}')
if [[ "$DEPLOYMENT_READY" == "$DEPLOYMENT_DESIRED" && "$DEPLOYMENT_READY" != "" ]]; then
    green "✅ Deployment prêt ($DEPLOYMENT_READY/$DEPLOYMENT_DESIRED replicas)"
else
    red "⚠️  Deployment pas prêt ($DEPLOYMENT_READY/$DEPLOYMENT_DESIRED replicas)"
fi
kubectl get deployment -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform
echo ""

echo "🔌 3. Services:"
kubectl get svc -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform
echo ""

echo "🌐 4. Ingress:"
kubectl get ingress -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform
echo ""

echo "📊 5. Métriques de santé:"
# Health check via port-forward
kubectl port-forward -n $NAMESPACE svc/${RELEASE_NAME}-network-security-platform 8080:8080 &
PF_PID=$!
sleep 3

if curl -s -f http://localhost:8080/actuator/health >/dev/null; then
    green "✅ Endpoint /actuator/health accessible"
    curl -s http://localhost:8080/actuator/health | jq . 2>/dev/null || curl -s http://localhost:8080/actuator/health
else
    red "❌ Endpoint /actuator/health inaccessible"
fi

kill $PF_PID 2>/dev/null || true
wait $PF_PID 2>/dev/null || true
echo ""

echo "🗂️  6. ConfigMaps et Secrets:"
kubectl get configmap -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform
kubectl get secret -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform
echo ""

echo "📈 7. Utilisation des ressources:"
kubectl top pods -n $NAMESPACE --containers 2>/dev/null || yellow "⚠️  Metrics server non disponible"
echo ""

# Résumé
echo "📋 Résumé:"
if [[ "$DEPLOYMENT_READY" == "$DEPLOYMENT_DESIRED" && "$DEPLOYMENT_READY" != "" ]]; then
    green "✅ Le déploiement est sain et prêt!"
else
    red "❌ Le déploiement a des problèmes. Vérifiez les logs avec:"
    echo "kubectl logs -n $NAMESPACE -l app.kubernetes.io/name=network-security-platform --tail=100"
fi