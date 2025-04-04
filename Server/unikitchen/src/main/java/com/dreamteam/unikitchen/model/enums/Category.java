
package com.dreamteam.unikitchen.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    KUCHEN,
    NUDELN,
    REIS,
    FLEISCH,
    VEGETARISCH;

    @JsonCreator
    public static Category fromString(String value) {
        return Category.valueOf(value.toUpperCase());
    }
}



