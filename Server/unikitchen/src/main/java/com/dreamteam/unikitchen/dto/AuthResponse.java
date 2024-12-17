package com.dreamteam.unikitchen.dto;

public record AuthResponse(
        String token,
        UserInfoResponse userInfo
) {}
