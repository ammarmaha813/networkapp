#!/bin/bash
set -e

# Configuration
NAMESPACE="security-dev"
RELEASE_NAME="network-security-dev"
CHART_PATH="../charts/network-security-platform"
VALUES_FILE="../charts/network-security-platform/values-dev.yaml"

echo "🚀 Déploiement Network Security Platform - Environnement DEV"
echo "📦 Namespace: $NAMESPACE"
echo "🔧 Release: $RELEASE_NAME"

# Créer le namespace s'il n'existe pas
echo "🏗️  Création du namespace..."
kubectl create namespace ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

# Ajouter les repositories Helm
echo "📥 Mise à jour des repositories Helm..."
helm repo add bitnami https://charts.bitnami.com/bitnami 2>/dev/null || true
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts 2>/dev/null || true
helm repo update

# Vérifier si c'est une installation ou une mise à jour
if helm list -n ${NAMESPACE} | grep -q ${RELEASE_NAME}; then
    echo "🔄 Mise à jour du déploiement existant..."
    ACTION="upgrade"
else
    echo "✨ Nouvelle installation..."
    ACTION="install"
fi

# Déployer avec Helm
echo "🎯 Exécution: helm $ACTION $RELEASE_NAME..."
helm ${ACTION} ${RELEASE_NAME} ${CHART_PATH} \
  -f ${VALUES_FILE} \
  --namespace ${NAMESPACE} \
  --wait \
  --timeout 15m \
  --atomic

# Vérifier le statut
echo "✅ Déploiement terminé!"
echo ""
echo "📊 Statut des pods:"
kubectl get pods -n ${NAMESPACE} -l app.kubernetes.io/name=network-security-platform
echo ""
echo "🔌 Services:"
kubectl get svc -n ${NAMESPACE} -l app.kubernetes.io/name=network-security-platform
echo ""
echo "🌐 Ingress:"
kubectl get ingress -n ${NAMESPACE} -l app.kubernetes.io/name=network-security-platform

# Afficher les notes
helm get notes ${RELEASE_NAME} -n ${NAMESPACE}