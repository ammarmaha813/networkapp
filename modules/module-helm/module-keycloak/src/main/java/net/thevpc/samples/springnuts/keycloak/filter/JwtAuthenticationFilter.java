/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.filter;

import com.eya.securityplatform.security.serviceImpl.LoggingServiceFactory;
import com.eya.securityplatform.security.servive.LoggingService;
import com.eya.securityplatform.security.servive.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author aya
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final TokenService tokenService;
    public JwtAuthenticationFilter(TokenService tokenService) {
    this.tokenService = tokenService;
}
           private final LoggingService loggerJAF = LoggingServiceFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Step 1: Extract JWT from header
            String jwt = extractJwtFromRequest(request);
            
            if (jwt != null) {
                loggerJAF.debug("JWT token found in request: {}", request.getRequestURI());
                
                // Step 2: Validate token
                if (tokenService.validateToken(jwt)) {
                    loggerJAF.debug("JWT token is valid");
                    
                    // Step 3: Extract user info
                    String username = tokenService.extractUsername(jwt);
                    List<String> roles = tokenService.extractRoles(jwt);
                    
                    loggerJAF.debug("Authenticated user: {} with roles: {}", username, roles);
                    
                    // Step 4: Convert roles to authorities
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    
                    // Step 5: Create authentication object
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );
                    
                    // Step 6: Set additional details
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Step 7: Set authentication in Security Context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    loggerJAF.info("User '{}' authenticated successfully for: {} {}",
                            username, request.getMethod(), request.getRequestURI());
                    
                } else {
                    loggerJAF.warn("JWT token validation failed for: {}", request.getRequestURI());
                }
            } else {
                loggerJAF.debug("No JWT token found in request: {}", request.getRequestURI());
            }
            
        } catch (Exception e) {
            loggerJAF.error("Cannot set user authentication: {}", e.getMessage(), e);
        }
        
        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * 
     * Expected format: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }  
    
}
