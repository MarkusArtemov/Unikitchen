package com.dreamteam.unikitchen.exception;

import java.time.LocalDateTime;

public record ApiExceptionResponse(
        String error,
        String message,
        int status,
        String timestamp
) {
    public ApiExceptionResponse(String error, String message, int status) {
        this(error, message, status, LocalDateTime.now().toString());
    }
}

