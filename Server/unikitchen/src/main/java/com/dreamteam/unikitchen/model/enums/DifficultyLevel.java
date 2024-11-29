package com.dreamteam.unikitchen.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DifficultyLevel {
    EINFACH,
    MITTEL,
    SCHWIERIG;

    @JsonCreator
    public static DifficultyLevel fromString(String value) {
        return DifficultyLevel.valueOf(value.toUpperCase());
    }
}
