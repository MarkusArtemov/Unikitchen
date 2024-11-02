package com.dreamteam.unikitchen.repository;

import com.dreamteam.unikitchen.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}
