/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.serviceImpl;


import com.eya.securityplatform.security.servive.LoggingService;
/**
 *
 * @author aya
 */
public class LoggingServiceFactory {
  
    
  /**
     * Get a logger for a specific class
     * 
     * @param clazz The class that will use this logger
     * @return LoggingService instance configured for that class
     * 
     * Example:
     * LoggingService logger = LoggingServiceFactory.getLogger(JwtUtil.class);
     * logger.info("This log will show: [JwtUtil] - This log message");
     */
    public static LoggingService getLogger(Class<?> clazz) {
        return new LoggingServiceImpl(clazz);
    }

    /**
     * Get a logger with a custom name
     * 
     * @param name Custom logger name
     * @return LoggingService instance with that name
     * 
     * Example:
     * LoggingService logger = LoggingServiceFactory.getLogger("SecurityModule");
     * logger.info("This log will show: [SecurityModule] - This log message");
     */
    public static LoggingService getLogger(String name) {
        return new LoggingServiceImpl(name);
    }
}
