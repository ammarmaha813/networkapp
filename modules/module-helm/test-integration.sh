#!/bin/bash
echo "🧪 Test de l'intégration Helm + Keycloak"

# نبنو الكل
echo "📦 Construction des modules..."
cd ~/sample-spring-nuts/modules/module-keycloak
mvn clean install -q

cd ~/sample-spring-nuts/modules/module-helm
mvn clean install -q

# نلانقو السيرڤر
echo "🚀 Démarrage de l'application intégrée..."
mvn spring-boot:run > app.log 2>&1 &
APP_PID=$!

# نستناو شوية
sleep 15

# نعملو الـ tests
echo "🧪 Tests d'intégration:"

echo "1. Health check global:"
curl -s http://localhost:8083/api/helm/health | jq . 2>/dev/null || echo "Pas de JSON"

echo "2. Test Keycloak integration:"
curl -s http://localhost:8083/api/helm/test | jq . 2>/dev/null || echo "Pas de JSON"

echo "3. Test sécurisé (devrait échouer sans auth):"
curl -s http://localhost:8083/api/helm/deployments/default/test-app | jq . 2>/dev/null || echo "Accès refusé (normal)"

echo "4. Test d'authentification:"
curl -s -X POST http://localhost:8083/api/helm/auth/test \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' | jq . 2>/dev/null || echo "Test auth"

# نوقفو السيرڤر
echo "🛑 Arrêt..."
kill $APP_PID 2>/dev/null || true
wait $APP_PID 2>/dev/null || true

echo "✅ Test d'intégration terminé!"
