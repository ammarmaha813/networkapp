 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.serviceImpl;

import com.eya.securityplatform.security.Util.JwtUtil;
import com.eya.securityplatform.security.config.SecurityProperties;
import com.eya.securityplatform.security.dto.UserInfo;
import com.eya.securityplatform.security.exception.JwtAuthenticationException;
import com.eya.securityplatform.security.servive.LoggingService;
import com.eya.securityplatform.security.servive.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TokenServiceImpl - JWT Token Management Implementation
 * 
 * This service is the BRAIN of your authentication system.
 * Think of it as the "ID Card Factory" for your SIEM platform.
 * 
 * KEY CONCEPTS:
 * 1. Access Token = Short-lived ID card (15-60 min) - for daily API access
 * 2. Refresh Token = Long-term membership card (7-30 days) - to get new access tokens
 * 3. Token Blacklist = List of cancelled/stolen ID cards
 * 
 * SECURITY FEATURES:
 * - JWT signature verification (prevents tampering)
 * - Expiration checking (auto-invalidate old tokens)
 * - Token revocation/blacklist (manual invalidation for logout/breach)
 * - Role-based claims (embed user permissions in token)
 */
@Service
/**
 *
 * @author aya
 */
public class TokenServiceImpl implements TokenService {
    
     private final JwtUtil jwtUtil;  // Low-level JWT operations (signing, parsing)
    private final SecurityProperties securityProperties;  // Configuration from YAML
           private final LoggingService logger = LoggingServiceFactory.getLogger(TokenServiceImpl.class);

    public TokenServiceImpl(JwtUtil jwtUtil,SecurityProperties securityProperties){
      this.jwtUtil=jwtUtil;
      this.securityProperties=securityProperties;
    }
    
    // Token blacklist - stores revoked tokens in memory
    // ConcurrentHashMap = thread-safe map for high-performance concurrent access
    // In production, use Redis for distributed systems
    private final Map<String, Date> tokenBlacklist = new ConcurrentHashMap<>();
    
    // Token type constants
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    
    /**
     * Generate Access Token (short-lived)
     * This is like a daily pass to your office building
     */
    @Override
    public String generateAccessToken(UserInfo userInfo) {
        logger.debug("Generating access token for user: {}", userInfo.getUsername());
        
        // Build claims (data stored inside the token)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userInfo.getId());
        claims.put("email", userInfo.getEmail());
        claims.put("roles", userInfo.getRoles());
        claims.put("type", TOKEN_TYPE_ACCESS);
        
        // Get expiration from config (convert minutes to milliseconds)
        long expirationMs = securityProperties.getJwt().getExpiration() * 60 * 1000;
        
        // Generate token using JwtUtil
        String token = jwtUtil.generateToken(
            userInfo.getUsername(),  // Subject (who the token is for)
            claims,
            expirationMs
        );
        
        logger.info("Access token generated for user: {} (expires in {} minutes)", 
                 userInfo.getUsername(), 
                 securityProperties.getJwt().getExpiration());
        
        return token;
    }

    /**
     * Generate Refresh Token (long-lived)
     * This is like your annual gym membership - use it to get daily passes
     */
    @Override
    public String generateRefreshToken(UserInfo userInfo) {
        logger.debug("Generating refresh token for user: {}", userInfo.getUsername());
        
        // Refresh tokens have minimal claims (just enough to identify user)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userInfo.getId());
        claims.put("type", TOKEN_TYPE_REFRESH);
        
        // Refresh tokens live much longer (7-30 days typical)
        // Here we use 7 days = 7 * 24 * 60 minutes
        long refreshExpirationMs = 7 * 24 * 60 * securityProperties.getJwt().getExpiration() * 1000;
        
        String token = jwtUtil.generateToken(
            userInfo.getUsername(),
            claims,
            refreshExpirationMs
        );
        
        logger.info("Refresh token generated for user: {}", userInfo.getUsername());
        
        return token;
    }

    /**
     * Generate both tokens at once (used during login)
     */
    @Override
    public Map<String, String> generateTokenPair(UserInfo userInfo) {
        logger.debug("Generating token pair for user: {}", userInfo.getUsername());
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateAccessToken(userInfo));
        tokens.put("refreshToken", generateRefreshToken(userInfo));
        
        return tokens;
    }

    /**
     * Generate custom token (for password reset, email verification, etc.)
     */
    @Override
    public String generateCustomToken(String subject, Map<String, Object> claims, long expirationMs) {
        logger.debug("Generating custom token for subject: {}", subject);
        return jwtUtil.generateToken(subject, claims, expirationMs);
    }

    /**
     * Validate token - THE SECURITY CHECKPOINT
     * This checks:
     * 1. Is the signature valid? (not tampered)
     * 2. Is it expired?
     * 3. Is it blacklisted?
     */
    @Override
    public boolean validateToken(String token) {
        try {
            // First check if token is blacklisted
            if (isTokenRevoked(token)) {
                logger.warn("Token validation failed: Token is blacklisted");
                return false;
            }
            
            // Use JwtUtil to validate signature and expiration
            boolean isValid = jwtUtil.validateToken(token);
            
            if (isValid) {
                logger.debug("Token validation successful");
            } else {
                logger.warn("Token validation failed");
            }
            
            return isValid;
            
        } catch (ExpiredJwtException e) {
            logger.warn("Token validation failed: Token expired");
            return false;
        } catch (JwtException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract username from token
     */
    @Override
    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }

   
    @Override
    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", String.class);
    }

   
    @Override
    public Claims extractAllClaims(String token) {
        return jwtUtil.extractAllClaims(token);
    }

    
    @Override
    public Date extractExpiration(String token) {
        return jwtUtil.extractExpiration(token);
    }

    
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

   
    @Override
    public String refreshAccessToken(String refreshToken) {
        logger.debug("Attempting to refresh access token");
        
        try {
            // Validate refresh token
            if (!validateToken(refreshToken)) {
                throw new JwtAuthenticationException("Invalid or expired refresh token");
            }
            
            // Check token type
            String tokenType = extractTokenType(refreshToken);
            if (!TOKEN_TYPE_REFRESH.equals(tokenType)) {
                throw new JwtAuthenticationException("Token is not a refresh token");
            }
            
            // Extract user info
            String username = extractUsername(refreshToken);
            String userId = extractUserId(refreshToken);
            
            // For refresh, we need to fetch full user info including roles
            // This requires UserService, but to avoid circular dependency,
            // we'll create a minimal UserInfo with cached data from refresh token
            // In production, inject UserService to fetch fresh user data
            
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(username);
            userInfo.setId(userId);
            // Note: Roles should be fetched fresh from database for security
            // This is a simplified version
            
            // Generate new access token
            String newAccessToken = generateAccessToken(userInfo);
            
            logger.info("Access token refreshed successfully for user: {}", username);
            
            return newAccessToken;
            
        } catch (JwtException e) {
            logger.error("Failed to refresh access token: {}", e.getMessage());
            throw new JwtAuthenticationException("Failed to refresh token", e);
        }
    }

  
    @Override
    public void revokeToken(String token) {
        try {
            Date expiration = extractExpiration(token);
            tokenBlacklist.put(token, expiration);
            
            logger.info("Token revoked and added to blacklist (expires: {})", expiration);
            
        } catch (JwtException e) {
            logger.warn("Failed to revoke token: {}", e.getMessage());
        }
    }

   
    @Override
    public boolean isTokenRevoked(String token) {
        return tokenBlacklist.containsKey(token);
    }

   
    @Override
    public boolean validateTokenAndRole(String token, String requiredRole) {
        if (!validateToken(token)) {
            return false;
        }
        
        List<String> roles = extractRoles(token);
        boolean hasRole = roles != null && roles.contains(requiredRole);
        
        logger.debug("Role validation for '{}': {}", requiredRole, hasRole);
        
        return hasRole;
    }

  
    @Override
    public void cleanupExpiredTokens() {
        Date now = new Date();
        AtomicInteger removedCount = new AtomicInteger(0);
        
        // Remove all tokens that have expired
        tokenBlacklist.entrySet().removeIf(entry -> {
            if (entry.getValue().before(now)) {
            removedCount.incrementAndGet();
                return true;
            }
            return false;
        });
        
        logger.info("Cleaned up {} expired tokens from blacklist", removedCount);
    }
}
