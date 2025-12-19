package com.eya.securityplatform.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // Tells Spring: "This is a configuration class"
@EnableWebSecurity  // Turns on Spring Security
public class WebSecurityConfig {

    @Bean  // Tells Spring: "Create this object for me"
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            // 1. Disable CSRF (like disabling "fight prevention" for API club)
            .csrf(csrf -> csrf.disable())
            
            // 2. Make it STATELESS (like a nightclub that doesn't remember you next night)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. WHO CAN GO WHERE? (The bouncer's rule book)
            .authorizeHttpRequests(authz -> authz
                // Public areas - anyone can enter
                .requestMatchers("/api/auth/**", "/api/public/**").permitAll()
                
                // Admin lounge - VIPs only
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Regular club area - paid members only
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                
                // Everywhere else - need ticket (authenticated)
                .anyRequest().authenticated()
            );
        
        return http.build();  // Build the security rules
    }
}