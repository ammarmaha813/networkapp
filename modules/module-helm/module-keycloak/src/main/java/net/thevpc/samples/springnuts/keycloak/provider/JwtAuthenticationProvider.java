/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.provider;


import com.eya.securityplatform.security.dto.UserInfo;
import com.eya.securityplatform.security.serviceImpl.LoggingServiceFactory;
import com.eya.securityplatform.security.servive.LoggingService;
import com.eya.securityplatform.security.servive.TokenService;
import com.eya.securityplatform.security.servive.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component

/**
 *
 * @author aya
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {
  
    private final TokenService tokenService;
    private final UserService userService;
    private final LoggingService logger = LoggingServiceFactory.getLogger(JwtAuthenticationProvider.class);

    
    public JwtAuthenticationProvider( TokenService tokenService, UserService userService) {
    this.tokenService = tokenService;
    this.userService = userService;
}

    /**
     * Authenticate using JWT token
     * 
     * @param authentication Contains JWT token as credentials
     * @return Authenticated token with user details
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("Attempting JWT authentication");
        
        try {
            // Extract JWT token from authentication object
            String jwtToken = (String) authentication.getCredentials();
            
            if (jwtToken == null || jwtToken.isEmpty()) {
                logger.warn("JWT token is null or empty");
                throw new BadCredentialsException("JWT token is required");
            }
            
            // Validate token
            if (!tokenService.validateToken(jwtToken)) {
                logger.warn("JWT token validation failed");
                throw new BadCredentialsException("Invalid or expired JWT token");
            }
            
            // Extract user information from token
            String username = tokenService.extractUsername(jwtToken);
            List<String> roles = tokenService.extractRoles(jwtToken);
            
            logger.debug("JWT token validated for user: {} with roles: {}", username, roles);
            
            // Optional: Fetch fresh user data from database
            // This ensures user still exists and is enabled
            UserInfo userInfo = userService.getUserByUsername(username)
                    .orElseThrow(() -> new BadCredentialsException("User not found: " + username));
            
            if (!userInfo.isEnabled()) {
                logger.warn("User account is disabled: {}", username);
                throw new BadCredentialsException("User account is disabled");
            }
            
            // Convert roles to Spring Security authorities
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            
            // Create authenticated token
            UsernamePasswordAuthenticationToken authenticatedToken = 
                    new UsernamePasswordAuthenticationToken(
                            username,           // Principal (who the user is)
                            jwtToken,          // Credentials (the JWT token)
                            authorities        // Authorities (what they can do)
                    );
            
            logger.info("JWT authentication successful for user: {}", username);
            
            return authenticatedToken;
            
        } catch (AuthenticationException e) {
            logger.error("JWT authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during JWT authentication: {}", e.getMessage(), e);
            throw new BadCredentialsException("Authentication failed", e);
        }
    }

    /**
     * This provider supports UsernamePasswordAuthenticationToken
     * where the credentials contain a JWT token
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
