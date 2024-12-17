package com.dreamteam.unikitchen.repository;

import com.dreamteam.unikitchen.model.Favorite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Finds all favorites by user ID
    List<Favorite> findByUserId(Long userId);

    // Finds a single favorite by user ID and recipe ID
    Favorite findByUserIdAndRecipeId(Long userId, Long recipeId);

    // Deletes a favorite by user ID and recipe ID
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);

    // Checks if a favorite exists for given user ID and recipe ID
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);

    // Deletes all favorites for a given recipe
    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.recipe.id = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);

}
