/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.Util;

import com.eya.securityplatform.security.config.SecurityProperties;
import com.eya.securityplatform.security.serviceImpl.LoggingServiceFactory;
import com.eya.securityplatform.security.servive.LoggingService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


@Component

/**
 *
 * @author aya
 */
public class JwtUtil {
   
    private final SecurityProperties securityProperties;
    
    public JwtUtil(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
    
    private static final String SECRET_KEY = "your-super-secret-jwt-signing-key-change-this-in-production-minimum-256-bits-required";

       private final LoggingService logger = LoggingServiceFactory.getLogger(JwtUtil.class);

    public String generateToken(String subject, Map<String, Object> claims, long expirationMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);
        
        logger.debug("Generating JWT token for subject: {} with expiration: {}", subject, expiration);
        
        return Jwts.builder()
                .setClaims(claims)                          // Add custom claims (userId, roles, etc.)
                .setSubject(subject)                         // Set subject (username)
                .setIssuer(securityProperties.getJwt().getIssuer())  // Who issued this token
                .setIssuedAt(now)                           // When token was created
                .setExpiration(expiration)                   // When token expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Sign with HMAC-SHA256
                .compact();                                  // Build and encode to string
    }

    
    public boolean validateToken(String token) {
        try {
            // Parse and validate token
            // If this doesn't throw an exception, token is valid
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Use our secret key to verify signature
                .build()
                .parseClaimsJws(token);          // Parse and validate
            
            logger.debug("Token validation successful");
            return true;
            
        } catch (ExpiredJwtException e) {
            // Token has expired (past the 'exp' claim)
            logger.warn("Token validation failed: Token expired at {}", e.getClaims().getExpiration());
            return false;
            
        } catch (UnsupportedJwtException e) {
            // Token format not supported
            logger.error("Token validation failed: Unsupported JWT - {}", e.getMessage());
            return false;
            
        } catch (MalformedJwtException e) {
            // Token structure is invalid
            logger.error("Token validation failed: Malformed JWT - {}", e.getMessage());
            return false;
            
        } catch (SignatureException e) {
            // Signature verification failed (token was tampered with!)
            logger.error("Token validation failed: Invalid signature - {}", e.getMessage());
            return false;
            
        } catch (IllegalArgumentException e) {
            // Token is null or empty
            logger.error("Token validation failed: Token is null or empty");
            return false;
            
        } catch (JwtException e) {
            // Any other JWT-related error
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

   
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

  
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();  // The payload/claims section
                    
        } catch (ExpiredJwtException e) {
          
            logger.debug("Extracting claims from expired token");
            return e.getClaims();
            
        } catch (JwtException e) {
            logger.error("Failed to extract claims: {}", e.getMessage());
            throw e;
        }
    }

   
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

  
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            boolean expired = expiration.before(new Date());
            
            if (expired) {
                logger.debug("Token expired at: {}", expiration);
            }
            
            return expired;
            
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

   
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
       
        return Keys.hmacShaKeyFor(keyBytes);
    }

   
    public String extractIssuer(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    
    public long getTimeUntilExpiration(String token) {
        Date expiration = extractExpiration(token);
        long timeRemaining = expiration.getTime() - System.currentTimeMillis();
        
        logger.debug("Token expires in {} milliseconds", timeRemaining);
        
        return timeRemaining;
    }

   
    public <T> T extractCustomClaim(String token, String claimName, Class<T> claimType) {
        Claims claims = extractAllClaims(token);
        return claims.get(claimName, claimType);
    }
    
}
