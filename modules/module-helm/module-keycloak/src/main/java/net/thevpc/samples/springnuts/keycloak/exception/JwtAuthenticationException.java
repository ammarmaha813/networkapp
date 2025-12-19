/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.exception;
import org.springframework.security.core.AuthenticationException;


/**
 *
 * @author aya
 */

public class JwtAuthenticationException  extends AuthenticationException {
    
 
    public JwtAuthenticationException(String message) {
        super(message);
    }

   
    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }  
}
