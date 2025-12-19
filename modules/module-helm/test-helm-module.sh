#!/bin/bash
echo "🧪 Test du module Helm"

# Nettoyer et compiler
echo "📦 Compilation..."
mvn clean install -q

# Vérifier si la compilation a réussi
if [ $? -ne 0 ]; then
    echo "❌ Compilation échouée"
    exit 1
fi

# Démarrer l'application
echo "🚀 Démarrage de l'application..."
mvn spring-boot:run > app.log 2>&1 &
APP_PID=$!

# Attendre le démarrage
echo "⏳ Attente du démarrage..."
sleep 15

# Fonction de test
test_endpoint() {
    local url=$1
    local description=$2
    echo "Testing $description..."
    if curl -s -f "$url" > /dev/null; then
        echo "✅ $description - OK"
        curl -s "$url" | jq . 2>/dev/null || echo "  Réponse: $(curl -s "$url")"
    else
        echo "❌ $description - Échec"
    fi
}

# Tests
test_endpoint "http://localhost:8083/actuator/health" "Spring Boot Health"
test_endpoint "http://localhost:8083/api/helm/health" "Helm Health"
test_endpoint "http://localhost:8083/api/helm/test" "Helm Test"
test_endpoint "http://localhost:8083/api/helm/info" "Helm Info"

# Afficher les logs en cas d'erreur
if [ $? -ne 0 ]; then
    echo "📋 Logs d'erreur:"
    tail -20 app.log
fi

# Arrêter l'application
echo "🛑 Arrêt de l'application..."
kill $APP_PID 2>/dev/null || true
wait $APP_PID 2>/dev/null || true

echo "✅ Tests terminés!"
