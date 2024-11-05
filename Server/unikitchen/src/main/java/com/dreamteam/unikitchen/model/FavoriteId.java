package com.dreamteam.unikitchen.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
public class FavoriteId implements Serializable {

    private Long userId;
    @Setter
    @Getter
    private Long recipeId;

    // Standard-Konstruktor
    public FavoriteId() {}

    public FavoriteId(Long userId, Long recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteId)) return false;
        FavoriteId that = (FavoriteId) o;
        return userId.equals(that.userId) && recipeId.equals(that.recipeId);
    }

    @Override
    public int hashCode() {
        return 31 * userId.hashCode() + recipeId.hashCode();
    }
}