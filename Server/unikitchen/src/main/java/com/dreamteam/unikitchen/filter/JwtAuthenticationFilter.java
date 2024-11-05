package com.dreamteam.unikitchen.filter;

import com.dreamteam.unikitchen.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/recipes/lastRecipes"
    );

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    private boolean isPublicPath(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::startsWith);
    }

    private String extractUsernameFromToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                return jwtUtil.validateTokenAndGetSubject(jwt);
            }
        } catch (Exception e) {
            logger.warn("Token-Validierung fehlgeschlagen: {}", e.getMessage());
            throw e;
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        if (isPublicPath(path) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        String username = extractUsernameFromToken(token);

        if (username != null) {
            UserPrincipalRequestWrapper wrappedRequest = new UserPrincipalRequestWrapper(request, username);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
