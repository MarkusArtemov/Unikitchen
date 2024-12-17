package com.dreamteam.unikitchen.dto;
import java.time.LocalDateTime;

public record UserInfoResponse(
        Long id,
        String username,
        String bio,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}


