#!/bin/bash
# Script de déploiement Helm pour Network Security Platform

RELEASE_NAME=${1:-"network-security"}
NAMESPACE=${2:-"security-system"}
VALUES_FILE=${3:-"values.yaml"}

echo "🚀 Déploiement Network Security Platform"
echo "📦 Release: $RELEASE_NAME"
echo "🎯 Namespace: $NAMESPACE"
echo "📋 Values: $VALUES_FILE"

# Ajouter les repos Helm nécessaires
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Créer le namespace
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Installer/Mettre à jour la release
helm upgrade --install $RELEASE_NAME . \
  --namespace $NAMESPACE \
  -f $VALUES_FILE \
  --wait \
  --timeout 15m

echo "✅ Déploiement terminé!"
