/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eya.securityplatform.security.filter;

import com.eya.securityplatform.security.serviceImpl.LoggingServiceFactory;
import com.eya.securityplatform.security.servive.LoggingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

@Component
/**
 *
 * @author aya
 */
public class RequestLoggingFilter extends OncePerRequestFilter {
        private final LoggingService loggerRLF = LoggingServiceFactory.getLogger(RequestLoggingFilter.class);

        @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        logRequest(requestWrapper);
        
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logResponse(responseWrapper, duration);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder logMessage = new StringBuilder();
        
        logMessage.append("\n========== INCOMING REQUEST ==========\n");
        logMessage.append(String.format("%s %s\n", request.getMethod(), request.getRequestURI()));
        
        String queryString = request.getQueryString();
        if (queryString != null) {
            logMessage.append(String.format("Query: %s\n", queryString));
        }
        
        logMessage.append(String.format("Client IP: %s\n", getClientIpAddress(request)));
        logMessage.append(String.format("User-Agent: %s\n", request.getHeader("User-Agent")));
        
        logMessage.append("Headers:\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            if (isSensitiveHeader(headerName)) {
                headerValue = "[REDACTED]";
            }
            
            logMessage.append(String.format("  %s: %s\n", headerName, headerValue));
        }
        
        String requestBody = getRequestBody(request);
        if (requestBody != null && !requestBody.isEmpty()) {
            logMessage.append("Body:\n");
            logMessage.append(maskSensitiveData(requestBody));
            logMessage.append("\n");
        }
        
        logMessage.append("======================================");
        
        loggerRLF.info(logMessage.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        StringBuilder logMessage = new StringBuilder();
        
        logMessage.append("\n========== OUTGOING RESPONSE ==========\n");
        logMessage.append(String.format("Status: %d %s\n", 
                response.getStatus(), 
                getStatusText(response.getStatus())));
        logMessage.append(String.format("Processing Time: %d ms\n", duration));
        
        logMessage.append("Headers:\n");
        response.getHeaderNames().forEach(headerName -> {
            String headerValue = response.getHeader(headerName);
            logMessage.append(String.format("  %s: %s\n", headerName, headerValue));
        });
        
        String responseBody = getResponseBody(response);
        if (responseBody != null && !responseBody.isEmpty()) {
            if (response.getStatus() >= 400 || responseBody.length() < 1000) {
                logMessage.append("Body:\n");
                logMessage.append(responseBody);
                logMessage.append("\n");
            } else {
                logMessage.append(String.format("Body: [%d bytes - truncated]\n", 
                        responseBody.length()));
            }
        }
        
        logMessage.append("======================================");
        
        if (response.getStatus() >= 500) {
            loggerRLF.error(logMessage.toString());
        } else if (response.getStatus() >= 400) {
            loggerRLF.warn(logMessage.toString());
        } else {
            loggerRLF.info(logMessage.toString());
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            try {
                return new String(content, request.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                loggerRLF.warn("Could not read request body: {}", e.getMessage());
            }
        }
        return null;
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            try {
                return new String(content, response.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                loggerRLF.warn("Could not read response body: {}", e.getMessage());
            }
        }
        return null;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }

    private boolean isSensitiveHeader(String headerName) {
        String lowerCase = headerName.toLowerCase();
        return lowerCase.contains("authorization") ||
               lowerCase.contains("password") ||
               lowerCase.contains("token") ||
               lowerCase.contains("secret") ||
               lowerCase.contains("api-key") ||
               lowerCase.contains("cookie");
    }

    private String maskSensitiveData(String body) {
        if (body == null) {
            return null;
        }
        
        body = body.replaceAll("(\"password\"\\s*:\\s*\")([^\"]+)(\")", "$1[REDACTED]$3");
        body = body.replaceAll("(\"token\"\\s*:\\s*\")([^\"]+)(\")", "$1[REDACTED]$3");
        body = body.replaceAll("(\"accessToken\"\\s*:\\s*\")([^\"]+)(\")", "$1[REDACTED]$3");
        body = body.replaceAll("(\"refreshToken\"\\s*:\\s*\")([^\"]+)(\")", "$1[REDACTED]$3");
        body = body.replaceAll("(\"apiKey\"\\s*:\\s*\")([^\"]+)(\")", "$1[REDACTED]$3");
        body = body.replaceAll("(\"api_key\"\\s*:\\s*\")([^\"]+)(\")", "$1[REDACTED]$3");
        
        return body;
    }

    private String getStatusText(int statusCode) {
            return switch (statusCode) {
                case 200 -> "OK";
                case 201 -> "Created";
                case 204 -> "No Content";
                case 400 -> "Bad Request";
                case 401 -> "Unauthorized";
                case 403 -> "Forbidden";
                case 404 -> "Not Found";
                case 500 -> "Internal Server Error";
                case 502 -> "Bad Gateway";
                case 503 -> "Service Unavailable";
                default -> "";
            };
    }
    
}
