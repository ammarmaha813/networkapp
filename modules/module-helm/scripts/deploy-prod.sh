#!/bin/bash
set -e

# Configuration
NAMESPACE="security-prod"
RELEASE_NAME="network-security-prod"
CHART_PATH="../charts/network-security-platform"
VALUES_FILE="../charts/network-security-platform/values-prod.yaml"

echo "🚀 Déploiement Network Security Platform - Environnement PRODUCTION"
echo "⚠️  ATTENTION: Ceci est un environnement de production!"
echo "📦 Namespace: $NAMESPACE"
echo "🔧 Release: $RELEASE_NAME"

# Demander confirmation
read -p "Êtes-vous sûr de vouloir déployer en production? (oui/non): " CONFIRM
if [[ "$CONFIRM" != "oui" ]]; then
    echo "❌ Déploiement annulé."
    exit 1
fi

# Vérifier les prérequis
echo "🔍 Vérification des prérequis..."
command -v helm >/dev/null 2>&1 || { echo "❌ Helm n'est pas installé"; exit 1; }
command -v kubectl >/dev/null 2>&1 || { echo "❌ kubectl n'est pas installé"; exit 1; }

# Créer le namespace
echo "🏗️  Création du namespace..."
kubectl create namespace ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

# Ajouter les repositories
echo "📥 Mise à jour des repositories Helm..."
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Backup si nécessaire
if helm list -n ${NAMESPACE} | grep -q ${RELEASE_NAME}; then
    echo "💾 Sauvegarde de la configuration actuelle..."
    helm get values ${RELEASE_NAME} -n ${NAMESPACE} > backup-values-${RELEASE_NAME}-$(date +%Y%m%d-%H%M%S).yaml
fi

# Déployer
echo "🎯 Déploiement en production..."
helm upgrade --install ${RELEASE_NAME} ${CHART_PATH} \
  -f ${VALUES_FILE} \
  --namespace ${NAMESPACE} \
  --wait \
  --timeout 30m \
  --atomic \
  --debug

# Vérifications post-déploiement
echo "🔍 Vérifications post-déploiement..."
kubectl get pods -n ${NAMESPACE} -l app.kubernetes.io/name=network-security-platform
kubectl get svc -n ${NAMESPACE} -l app.kubernetes.io/name=network-security-platform
kubectl get ingress -n ${NAMESPACE} -l app.kubernetes.io/name=network-security-platform

# Test de santé
echo "🧪 Test de santé..."
kubectl wait --for=condition=ready pod \
  -l app.kubernetes.io/name=network-security-platform \
  -n ${NAMESPACE} \
  --timeout=300s

echo "✅ Déploiement production terminé avec succès!"