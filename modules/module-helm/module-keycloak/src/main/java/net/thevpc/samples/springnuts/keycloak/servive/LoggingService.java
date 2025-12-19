/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eya.securityplatform.security.servive;

/**
 *
 * @author aya
 */
public interface LoggingService {
    
     /**
     * Log DEBUG message
     * Use for: Detailed debugging information
     * Example: "JWT token found in request", "Validating token for user: john"
     */
    void debug(String message);

    /**
     * Log DEBUG message with parameters
     * Example: debug("User {} authenticated with roles {}", username, roles)
     */
    void debug(String message, Object... args);

    /**
     * Log INFO message
     * Use for: General information, normal operations
     * Example: "User authenticated successfully", "Application started"
     */
    void info(String message);

    /**
     * Log INFO message with parameters
     * Example: info("User '{}' logged in from IP {}", username, ip)
     */
    void info(String message, Object... args);

    /**
     * Log WARN message
     * Use for: Warnings, potential problems
     * Example: "Token validation failed", "Account locked"
     */
    void warn(String message);

    /**
     * Log WARN message with parameters
     * Example: warn("Failed login attempt for user: {}", username)
     */
    void warn(String message, Object... args);

    /**
     * Log ERROR message
     * Use for: Errors, exceptions
     * Example: "Cannot connect to database", "Authentication failed"
     */
    void error(String message);

    /**
     * Log ERROR message with parameters
     * Example: error("Failed to process request: {}", errorMessage)
     */
    void error(String message, Object... args);

    /**
     * Log ERROR message with exception
     * Example: error("Database error: {}", e.getMessage(), e)
     */
    void error(String message, Throwable throwable);

    /**
     * Log ERROR message with parameters and exception
     */
    void error(String message, Object arg, Throwable throwable);

    /**
     * Log TRACE message (very detailed)
     * Use for: Very detailed debugging
     */
    void trace(String message);

    /**
     * Log TRACE message with parameters
     */
    void trace(String message, Object... args);

    /**
     * Check if DEBUG level is enabled
     * Useful to avoid expensive string operations if debug is disabled
     */
    boolean isDebugEnabled();

    /**
     * Check if INFO level is enabled
     */
    boolean isInfoEnabled();

    /**
     * Check if WARN level is enabled
     */
    boolean isWarnEnabled();

    /**
     * Check if ERROR level is enabled
     */
    boolean isErrorEnabled();

    /**
     * Check if TRACE level is enabled
     */
    boolean isTraceEnabled();
    
}
