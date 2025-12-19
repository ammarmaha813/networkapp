package com.eya.securityplatform.security.dto;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author aya
 */
public class AuthResponse {
    
 private String accessToken;   // Short-lived JWT (15-60 minutes)
    private String refreshToken;  // Long-lived JWT (7-30 days)
    private String tokenType;     // Always "Bearer"
    private Integer expiresIn;    // Access token expiration in seconds
    private UserInfo userInfo;    // User details

    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor
     * Sets tokenType to "Bearer" by default
     */
    public AuthResponse() {
        this.tokenType = "Bearer";
    }

    /**
     * Full constructor
     */
    public AuthResponse(String accessToken, String refreshToken, Integer expiresIn, UserInfo userInfo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    @Override
    public String toString() {
        return "AuthResponse{" +
                "accessToken='" + (accessToken != null ? accessToken.substring(0, Math.min(20, accessToken.length())) + "..." : "null") + '\'' +
                ", refreshToken='" + (refreshToken != null ? refreshToken.substring(0, Math.min(20, refreshToken.length())) + "..." : "null") + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", userInfo=" + userInfo +
                '}';
    }
}
