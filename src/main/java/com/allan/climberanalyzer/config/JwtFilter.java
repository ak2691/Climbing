package com.allan.climberanalyzer.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.allan.climberanalyzer.UserHandling.service.CustomUserDetailsService;
import com.allan.climberanalyzer.UserHandling.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/login",
            "/api/register",
            "/api/refresh",
            "/login",
            "/register",
            "/api/verifyuser",
            "/api/calculator",
            "/api/logout"

    // Add any other public endpoints here
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestPath = request.getServletPath();
        String method = request.getMethod();

        if (EXCLUDED_PATHS.contains(requestPath)) {

            filterChain.doFilter(request, response);
            return;
        }

        String tokenHeader = request.getHeader("Authorization");

        String jwtToken = null;
        String username = null;

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            jwtToken = tokenHeader.substring(7);
            logger.info("JWT Filter - Extracted token: {}...",
                    jwtToken.substring(0, Math.min(20, jwtToken.length())));

            try {
                username = jwtService.getUsernameFromToken(jwtToken);
                logger.info("JWT Filter - Extracted username: {}", username);
            } catch (IllegalArgumentException e) {
                logger.error("JWT Filter - Unable to get JWT token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.error("JWT Filter - JWT token expired: {}", e.getMessage());
                logger.error("JWT Filter - Token was: {}...", jwtToken.substring(0, Math.min(20, jwtToken.length())));
            } catch (Exception e) {
                logger.error("JWT Filter - Unexpected JWT error: {}", e.getMessage());
            }
        } else {
            logger.warn("JWT Filter - No Bearer token found for: {}", requestPath);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                boolean isValid = jwtService.validateAccessToken(jwtToken, userDetails);

                if (isValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } else {
                    logger.warn("JWT Filter - Token validation failed for user: {}", username);
                }
            } catch (Exception e) {
                logger.error("JWT Filter - Error during validation: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
