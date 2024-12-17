package com.dreamteam.unikitchen.repository;

import com.dreamteam.unikitchen.model.Rating;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Finds all ratings for a given recipe ID
    List<Rating> findByRecipeId(Long recipeId);

    // Finds all ratings by a given user ID
    List<Rating> findByUserId(Long userId);

    // Finds a rating by a user and a recipe
    Optional<Rating> findByUserAndRecipe(User user, Recipe recipe);

    // Counts how many ratings a recipe has
    int countByRecipeId(Long recipeId);

    // Deletes all ratings of a given recipe ID
    @Modifying
    @Transactional
    @Query("DELETE FROM Rating r WHERE r.recipe.id = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);

}
