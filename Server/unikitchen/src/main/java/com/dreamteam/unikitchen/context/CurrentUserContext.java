package com.dreamteam.unikitchen.context;

import lombok.Getter;

import java.util.Optional;

public final class CurrentUserContext {

    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    // Set current user info in the context
    public static void setCurrentUsername(String username) {
        currentUser.set(username);
    }

    // Get the username of the current user
    public static String getCurrentUsername() {
        return currentUser.get();
    }

    // Clear the current context
    public static void clear() {
        currentUser.remove();
    }
}
