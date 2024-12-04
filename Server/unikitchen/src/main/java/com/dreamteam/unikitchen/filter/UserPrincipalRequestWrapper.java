package com.dreamteam.unikitchen.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;

public class UserPrincipalRequestWrapper extends HttpServletRequestWrapper {

    // The username to be injected as the principal
    private final String username;

    // Constructor to initialize the wrapper and inject the username
    public UserPrincipalRequestWrapper(HttpServletRequest request, String username) {
        super(request);
        this.username = username;
    }

    // Overrides the getUserPrincipal method to provide the injected username
    @Override
    public Principal getUserPrincipal() {
        return () -> username;
    }
}
