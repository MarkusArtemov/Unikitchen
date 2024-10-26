package com.dreamteam.unikitchen.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;

public class UserPrincipalRequestWrapper extends HttpServletRequestWrapper {
    private final String username;

    public UserPrincipalRequestWrapper(HttpServletRequest request, String username) {
        super(request);
        this.username = username;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> username;
    }
}

