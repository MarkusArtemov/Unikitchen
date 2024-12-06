package com.dreamteam.unikitchen.repository;

import com.dreamteam.unikitchen.model.Rating;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRecipeId(Long recipeId);

    List<Rating> findByUserId(Long userId);

    Optional<Rating> findByUserAndRecipe(User user, Recipe recipe);

    int countByRecipeId(Long recipeId);
}
