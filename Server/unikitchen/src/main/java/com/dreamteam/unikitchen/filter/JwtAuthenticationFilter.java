package com.dreamteam.unikitchen.filter;

import com.dreamteam.unikitchen.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // List of public URLs that don't require authentication
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/recipes/lastRecipes",
            "/api/recipes/[^/]+/recipe-image"
    );

    private final JwtUtil jwtUtil;

    // Constructor injection of JwtUtil for token validation
    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Checks if the requested path is in the list of public URLs
    private boolean isPublicPath(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::matches);
    }

    // Extracts the username from the JWT token if valid
    private String extractUsernameFromToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7); // Remove "Bearer " prefix
                return jwtUtil.validateTokenAndGetSubject(jwt); // Validate and extract subject
            }
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            throw e;
        }
        return null; // Return null if token is invalid or not present
    }

    // Main filter logic that runs once per request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI(); // Get the request path

        // Allow public paths and OPTIONS requests to bypass the filter
        if (isPublicPath(path) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token from the Authorization header
        String token = request.getHeader("Authorization");
        String username = extractUsernameFromToken(token); // Extract username from token

        if (username != null) {
            // Wrap the request with the authenticated user's principal
            UserPrincipalRequestWrapper wrappedRequest = new UserPrincipalRequestWrapper(request, username);
            filterChain.doFilter(wrappedRequest, response); // Continue with the wrapped request
        } else {
            // Respond with 401 Unauthorized if token is invalid
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
