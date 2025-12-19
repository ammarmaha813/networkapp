/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.Util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
/**
 *
 * @author aya
 */
public class SecurityContextUtil {
    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        // Extract username from principal
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            // If using UserDetails (standard Spring Security)
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            // If principal is just a string (simple authentication)
            return Optional.of((String) principal);
        }
        
        // Unknown principal type
        return Optional.empty();
    }

   
    public static Optional<Authentication> getCurrentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication);
    }

    
    public static Optional<String> getCurrentUserId() {
        return getCurrentAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserDetails)
                .map(principal -> {
                    // If your UserDetails has a getId() method, extract it
                    // This is a simplified version - adjust based on your implementation
                    if (principal.toString().contains("id=")) {
                        // Parse from toString() as fallback
                        return principal.toString();
                    }
                    return null;
                });
    }

    
    public static boolean hasRole(String role) {
        return getCurrentAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> {
                            String authority = grantedAuthority.getAuthority();
                            // Check both with and without ROLE_ prefix
                            return authority.equals("ROLE_" + role) || authority.equals(role);
                        }))
                .orElse(false);
    }

    
    public static boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    
    public static boolean hasAllRoles(String... roles) {
        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    
    public static Set<String> getCurrentUserRoles() {
        return getCurrentAuthentication()
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.startsWith("ROLE_") ? 
                        authority.substring(5) : authority)  // Remove ROLE_ prefix
                .collect(Collectors.toSet());
    }

    
    public static boolean isAuthenticated() {
        return getCurrentAuthentication()
                .map(auth -> auth.isAuthenticated() && 
                        !"anonymousUser".equals(auth.getPrincipal()))
                .orElse(false);
    }

    
    public static Optional<UserDetails> getCurrentUserDetails() {
        return getCurrentAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserDetails)
                .map(principal -> (UserDetails) principal);
    }

    
    public static boolean hasAuthority(String authority) {
        return getCurrentAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> 
                                grantedAuthority.getAuthority().equals(authority)))
                .orElse(false);
    }

   
    public static Set<String> getCurrentUserAuthorities() {
        return getCurrentAuthentication()
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    
    public static boolean isAnonymous() {
        return !isAuthenticated();
    }

   
    public static void clearContext() {
        SecurityContextHolder.clearContext();
    }

   
    public static Optional<Object> getAuthenticationDetails() {
        return getCurrentAuthentication()
                .map(Authentication::getDetails);
    }

   
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

 
    public static Optional<Object> getCurrentPrincipal() {
        return getCurrentAuthentication()
                .map(Authentication::getPrincipal);
    }
    
}
