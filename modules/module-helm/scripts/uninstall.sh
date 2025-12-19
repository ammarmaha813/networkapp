#!/bin/bash
set -e

# Usage: ./uninstall.sh [namespace] [release-name]

NAMESPACE="${1:-security-dev}"
RELEASE_NAME="${2:-network-security-dev}"

echo "🗑️  Désinstallation du déploiement"
echo "📦 Namespace: $NAMESPACE"
echo "🔧 Release: $RELEASE_NAME"

# Demander confirmation
read -p "Êtes-vous sûr de vouloir désinstaller $RELEASE_NAME? (oui/non): " CONFIRM
if [[ "$CONFIRM" != "oui" ]]; then
    echo "❌ Désinstallation annulée."
    exit 1
fi

# Vérifier si la release existe
if helm list -n ${NAMESPACE} | grep -q ${RELEASE_NAME}; then
    echo "🔄 Désinstallation de la release Helm..."
    helm uninstall ${RELEASE_NAME} -n ${NAMESPACE}

    echo "⏳ Attente de la suppression des ressources..."
    kubectl wait --for=delete pod -l app.kubernetes.io/name=network-security-platform -n ${NAMESPACE} --timeout=300s 2>/dev/null || true

    echo "✅ Désinstallation terminée!"
else
    echo "⚠️  Release $RELEASE_NAME non trouvée dans le namespace $NAMESPACE"
fi

# Optionnel: supprimer le namespace
read -p "Voulez-vous supprimer le namespace $NAMESPACE? (oui/non): " DELETE_NS
if [[ "$DELETE_NS" == "oui" ]]; then
    echo "🗑️  Suppression du namespace..."
    kubectl delete namespace ${NAMESPACE} --ignore-not-found=true
    echo "✅ Namespace supprimé!"
fi