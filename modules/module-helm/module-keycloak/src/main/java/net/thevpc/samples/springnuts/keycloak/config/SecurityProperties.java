package com.eya.securityplatform.security.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration  // Tells Spring: "Load this class at startup"
@ConfigurationProperties(prefix = "security")  
// Maps all settings under "security:" from the YAML file

public class SecurityProperties {

        // ------------ KEYCLOAK SETTINGS ----------------
    private KeycloakProperties keycloak = new KeycloakProperties();
    private JwtProperties jwt = new JwtProperties();

    // ------------- CORS SETTINGS -------------------
    private CorsProperties cors = new CorsProperties();

    // Getters and Setters for main properties
    public KeycloakProperties getKeycloak() {
        return keycloak;
    }

    public void setKeycloak(KeycloakProperties keycloak) {
        this.keycloak = keycloak;
    }

    public JwtProperties getJwt() {
        return jwt;
    }

    public void setJwt(JwtProperties jwt) {
        this.jwt = jwt;
    }

    public CorsProperties getCors() {
        return cors;
    }

    public void setCors(CorsProperties cors) {
        this.cors = cors;
    }

    // ------------ KEYCLOAK PROPERTIES INNER CLASS ----------------
    public static class KeycloakProperties {
        private String serverUrl;     // ex: http://localhost:8080
        private String realm;         // ex: security-platform
        private String clientId;      // backend-service
        private String clientSecret;  // secret value

        public String getServerUrl() {
            return serverUrl;
        }

        public void setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
        }

        public String getRealm() {
            return realm;
        }

        public void setRealm(String realm) {
            this.realm = realm;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }

    // ------------ JWT PROPERTIES INNER CLASS ---------------------
    public static class JwtProperties {
        private String issuer;
        private Long expiration;  // in minutes

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public Long getExpiration() {
            return expiration;
        }

        public void setExpiration(Long expiration) {
            this.expiration = expiration;
        }
    }

    // ------------- CORS PROPERTIES INNER CLASS -------------------
    public static class CorsProperties {
        private String[] allowedOrigins;  // ex: ["http://localhost:3000"]
        private String[] allowedMethods;
        private String[] allowedHeaders;

        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String[] allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public String[] getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(String[] allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public String[] getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(String[] allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }
    }}
