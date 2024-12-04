package com.dreamteam.unikitchen.dto;

public record AuthRequest(
        String username,
        String password,
        String bio
) {}

