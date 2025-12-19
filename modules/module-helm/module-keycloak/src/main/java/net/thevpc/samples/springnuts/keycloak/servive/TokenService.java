/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eya.securityplatform.security.servive;
import com.eya.securityplatform.security.dto.UserInfo;
import com.eya.securityplatform.security.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Map;
/**
 *
 * @author aya
 */
public interface TokenService {
 
    
    String generateAccessToken(UserInfo userInfo);

   
    String generateRefreshToken(UserInfo userInfo);

   
    Map<String, String> generateTokenPair(UserInfo userInfo);

   
    String generateCustomToken(String subject, Map<String, Object> claims, long expirationMs);

    
    boolean validateToken(String token);

    
    String extractUsername(String token);

   
    String extractUserId(String token);

    Claims extractAllClaims(String token);

    Date extractExpiration(String token);

    
    boolean isTokenExpired(String token);

   
    String refreshAccessToken(String refreshToken);

    void revokeToken(String token);

    
    boolean isTokenRevoked(String token);

   
    boolean validateTokenAndRole(String token, String requiredRole);

   
    void cleanupExpiredTokens();

   
    default java.util.List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", java.util.List.class);
    }

  
    default String extractTokenType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("type", String.class);
    }

    
    default Claims validateAndExtractClaims(String token) {
        if (!validateToken(token)) {
            throw new JwtAuthenticationException("Invalid token");
        }
        return extractAllClaims(token);
    }

  
    default String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

  
    default boolean isTokenType(String token, String expectedType) {
        String actualType = extractTokenType(token);
        return expectedType != null && expectedType.equals(actualType);
    }
    
}
