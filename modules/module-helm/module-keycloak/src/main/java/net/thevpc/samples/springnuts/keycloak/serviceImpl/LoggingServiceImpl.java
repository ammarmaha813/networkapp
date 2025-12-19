/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.serviceImpl;


import com.eya.securityplatform.security.servive.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
/**
 *
 * @author aya
 */
public class LoggingServiceImpl  implements LoggingService {
    
    private final Logger logger;

    /**
     * Constructor - creates logger for the calling class
     * You can inject this service into any class and it will log with that class's name
     */
    public LoggingServiceImpl() {
        // Get logger for this service
        this.logger = LoggerFactory.getLogger(LoggingServiceImpl.class);
    }

    /**
     * Constructor with custom logger name
     * Useful if you want logs to show a specific class name
     */
    public LoggingServiceImpl(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Constructor with custom logger name as string
     */
    public LoggingServiceImpl(String loggerName) {
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    // ============================================================
    // DEBUG LEVEL
    // ============================================================

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    // ============================================================
    // INFO LEVEL
    // ============================================================

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    // ============================================================
    // WARN LEVEL
    // ============================================================

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    // ============================================================
    // ERROR LEVEL
    // ============================================================

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    public void error(String message, Object arg, Throwable throwable) {
        logger.error(message, arg, throwable);
    }

    // ============================================================
    // TRACE LEVEL
    // ============================================================

    @Override
    public void trace(String message) {
        logger.trace(message);
    }

    @Override
    public void trace(String message, Object... args) {
        logger.trace(message, args);
    }

    // ============================================================
    // CHECK IF LEVEL IS ENABLED
    // ============================================================

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    /**
     * Get the underlying SLF4J logger
     * Useful if you need direct access to logger for advanced features
     */
    public Logger getLogger() {
        return logger;
    }
}
