/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.serviceImpl;



import com.eya.securityplatform.security.config.SecurityProperties;
import com.eya.securityplatform.security.dto.AuthRequest;
import com.eya.securityplatform.security.dto.AuthResponse;
import com.eya.securityplatform.security.dto.UserInfo;
import com.eya.securityplatform.security.exception.JwtAuthenticationException;
import com.eya.securityplatform.security.servive.LoggingService;
import com.eya.securityplatform.security.servive.UserService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import org.springframework.web.client.RestClientException;

@Service("keycloakUserService")  // Named bean for disambiguation


/**
 *
 * @author aya
 */
public class KeycloakUserService implements UserService {
 
    
    private final SecurityProperties securityProperties ;
    private final RestTemplate restTemplate;  
    private final LoggingService logger = LoggingServiceFactory.getLogger(KeycloakUserService.class);

   public KeycloakUserService(SecurityProperties securityProperties,RestTemplate restTemplate){
       this.restTemplate=restTemplate;
       this.securityProperties=securityProperties;
   }
           
           
    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        logger.info("Authenticating user via Keycloak: {}", authRequest.getUsername());
        
        try {
            // Build Keycloak token endpoint URL
            String tokenUrl = buildTokenUrl();
            
            // Prepare request body (OAuth2 password grant)
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "password");
            requestBody.add("client_id", securityProperties.getKeycloak().getClientId());
            requestBody.add("client_secret", securityProperties.getKeycloak().getClientSecret());
            requestBody.add("username", authRequest.getUsername());
            requestBody.add("password", authRequest.getPassword());
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);
            
            // Call Keycloak
            ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            Map<String, Object> tokenResponse = response.getBody();
            
            if (tokenResponse == null) {
                throw new JwtAuthenticationException("Empty response from Keycloak");
            }
            
            // Extract tokens
            String accessToken = (String) tokenResponse.get("access_token");
            String refreshToken = (String) tokenResponse.get("refresh_token");
            Integer expiresIn = (Integer) tokenResponse.get("expires_in");
            
            // Fetch user info from Keycloak
            UserInfo userInfo = fetchUserInfo(accessToken);
            
            // Build response
            AuthResponse authResponse = new AuthResponse();
            authResponse.setAccessToken(accessToken);
            authResponse.setRefreshToken(refreshToken);
            authResponse.setTokenType("Bearer");
            authResponse.setExpiresIn(expiresIn);
            authResponse.setUserInfo(userInfo);
            
            logger.info("User authenticated successfully via Keycloak: {}", authRequest.getUsername());
            
            return authResponse;
            
        } catch (HttpClientErrorException.Unauthorized e) {
            logger.warn("Keycloak authentication failed: Invalid credentials for user {}", 
                     authRequest.getUsername());
            throw new JwtAuthenticationException("Invalid credentials");
            
        } catch (HttpClientErrorException e) {
            logger.error("Keycloak HTTP error: {} - {}", e.getStatusCode(), e.getMessage());
            throw new JwtAuthenticationException("Authentication service error", e);
            
        } catch (Exception e) {
            logger.error("Keycloak authentication error: {}", e.getMessage(), e);
            throw new JwtAuthenticationException("Authentication failed", e);
        }
    }

    
    @Override
    public UserInfo registerUser(String username, String email, String password, List<String> roles) {
        logger.info("Registering new user in Keycloak: {}", username);
        
        try {
            // 1. Get admin access token
            String adminToken = getAdminAccessToken();
            
            // 2. Build user creation request
            String usersUrl = buildAdminUsersUrl();
            
            Map<String, Object> userRepresentation = new HashMap<>();
            userRepresentation.put("username", username);
            userRepresentation.put("email", email);
            userRepresentation.put("enabled", true);
            userRepresentation.put("emailVerified", false);
            
            // Set credentials
            Map<String, Object> credential = new HashMap<>();
            credential.put("type", "password");
            credential.put("value", password);
            credential.put("temporary", false);  // User doesn't need to change password
            userRepresentation.put("credentials", Collections.singletonList(credential));
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userRepresentation, headers);
            
            // Create user
            ResponseEntity<Void> response = restTemplate.exchange(
                usersUrl,
                HttpMethod.POST,
                entity,
                Void.class
            );
            
            if (response.getStatusCode() == HttpStatus.CREATED) {
                logger.info("User created successfully in Keycloak: {}", username);
                
                // Extract user ID from Location header
                String location = response.getHeaders().getLocation().toString();
                String userId = location.substring(location.lastIndexOf('/') + 1);
                
                // Assign roles
                if (roles != null && !roles.isEmpty()) {
                    assignRolesToUser(userId, roles, adminToken);
                }
                
                // Build and return UserInfo
                UserInfo userInfo = new UserInfo();
                userInfo.setId(userId);
                userInfo.setUsername(username);
                userInfo.setEmail(email);
                userInfo.setEnabled(true);
                userInfo.setRoles(roles);
                
                return userInfo;
            } else {
                throw new JwtAuthenticationException("Failed to create user in Keycloak");
            }
            
        } catch (JwtAuthenticationException | RestClientException e) {
            logger.error("Failed to register user in Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("User registration failed", e);
        }
    }

    @Override
    public Optional<UserInfo> getUserByUsername(String username) {
        logger.debug("Fetching user from Keycloak: {}", username);
        
        try {
            String adminToken = getAdminAccessToken();
            String usersUrl = buildAdminUsersUrl() + "?username=" + username;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                usersUrl,
                HttpMethod.GET,
                entity,
                List.class
            );
            
            List<Map<String, Object>> users = response.getBody();
            if (users != null && !users.isEmpty()) {
                return Optional.of(mapToUserInfo(users.get(0)));
            }
            
            return Optional.empty();
            
        } catch (RestClientException e) {
            logger.error("Failed to fetch user from Keycloak: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserInfo> getUserById(String userId) {
        // TODO: Implement Keycloak API call
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<UserInfo> getUserByEmail(String email) {
        // TODO: Implement Keycloak API call
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public UserInfo updateUser(String userId, UserInfo userInfo) {
        throw new UnsupportedOperationException("Use Keycloak Admin Console");
    }

    @Override
    public boolean deleteUser(String userId) {
        // TODO: Call Keycloak Admin API to delete user
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean hasRole(String userId, String role) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<String> getUserRoles(String userId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void assignRole(String userId, String role) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeRole(String userId, String role) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean existsByUsername(String username) {
        return getUserByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return getUserByEmail(email).isPresent();
    }

    @Override
    public void setUserEnabled(String userId, boolean enabled) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<UserInfo> getAllUsers(int page, int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<UserInfo> searchUsers(String keyword, int page, int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ==================== HELPER METHODS ====================

    /**
     * Build Keycloak token endpoint URL
     */
    private String buildTokenUrl() {
        return String.format("%s/realms/%s/protocol/openid-connect/token",
            securityProperties.getKeycloak().getServerUrl(),
            securityProperties.getKeycloak().getRealm()
        );
    }

    /**
     * Build Keycloak userinfo endpoint URL
     */
    private String buildUserInfoUrl() {
        return String.format("%s/realms/%s/protocol/openid-connect/userinfo",
            securityProperties.getKeycloak().getServerUrl(),
            securityProperties.getKeycloak().getRealm()
        );
    }

    /**
     * Build Keycloak Admin API users endpoint
     */
    private String buildAdminUsersUrl() {
        return String.format("%s/admin/realms/%s/users",
            securityProperties.getKeycloak().getServerUrl(),
            securityProperties.getKeycloak().getRealm()
        );
    }

    /**
     * Fetch user info from Keycloak using access token
     */
    private UserInfo fetchUserInfo(String accessToken) {
        String userInfoUrl = buildUserInfoUrl();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            userInfoUrl,
            HttpMethod.GET,
            entity,
            Map.class
        );
        
        Map<String, Object> userInfoMap = response.getBody();
        return mapToUserInfo(userInfoMap);
    }

    /**
     * Get admin access token for Keycloak Admin API calls
     */
    private String getAdminAccessToken() {
        // This requires admin credentials configured separately
        // For production, use service account or admin user
        throw new UnsupportedOperationException("Configure admin credentials");
    }

    /**
     * Assign roles to user
     */
    private void assignRolesToUser(String userId, List<String> roles, String adminToken) {
        // TODO: Implement role assignment via Keycloak Admin API
        logger.info("Assigning roles to user {}: {}", userId, roles);
    }

    /**
     * Map Keycloak response to UserInfo DTO
     */
    private UserInfo mapToUserInfo(Map<String, Object> keycloakUser) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId((String) keycloakUser.get("sub"));
        userInfo.setUsername((String) keycloakUser.get("preferred_username"));
        userInfo.setEmail((String) keycloakUser.get("email"));
        userInfo.setEnabled(true);
        
        // Extract roles from realm_access or resource_access
        Object realmAccess = keycloakUser.get("realm_access");
        if (realmAccess instanceof Map) {
            Object rolesObj = ((Map<?, ?>) realmAccess).get("roles");
            if (rolesObj instanceof List) {
                userInfo.setRoles((List<String>) rolesObj);
            }
        }
        
        return userInfo;
    }
}
