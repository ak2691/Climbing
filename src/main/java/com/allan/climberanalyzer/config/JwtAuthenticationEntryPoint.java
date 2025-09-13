package com.allan.climberanalyzer.config;

import java.io.IOException;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        logger.error("🚨 AUTHENTICATION ENTRY POINT - 401 Unauthorized error occurred");
        logger.error("🚨 Request URI: {}", request.getRequestURI());
        logger.error("🚨 Request Method: {}", request.getMethod());
        logger.error("🚨 Exception: {}", authException.getMessage());
        logger.error("🚨 Exception Type: {}", authException.getClass().getSimpleName());
        logger.error("🚨 Authorization Header: {}", request.getHeader("Authorization"));
        logger.error("🚨 SecurityContext Authentication: {}", SecurityContextHolder.getContext().getAuthentication());

        // Print stack trace to see where this is coming from
        logger.error("🚨 Stack trace:", authException);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}