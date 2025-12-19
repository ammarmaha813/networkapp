/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.exception;



/**
 *
 * @author aya
 */
public class SecurityConfigurationException extends RuntimeException {
    
 
    public SecurityConfigurationException(String message) {
        super(message);
    }

   
    public SecurityConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
