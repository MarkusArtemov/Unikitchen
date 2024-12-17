package com.dreamteam.unikitchen.repository;

import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByUserId(Long userId);

    Page<Recipe> findAll(Pageable pageable);

    List<Recipe> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT r FROM Recipe r WHERE r.id = :recipeId AND r.user.username = :username")
    Optional<Recipe> findByRecipeIdAndUsername(@Param("recipeId") Long recipeId, @Param("username") String username);

    @Query("SELECT r FROM Recipe r WHERE " +
            "(:maxDuration IS NULL OR r.duration <= :maxDuration) AND " +
            "(:difficultyLevel IS NULL OR r.difficultyLevel = :difficultyLevel) AND " +
            "(:category IS NULL OR r.category = :category) AND " +
            "(:maxPrice IS NULL OR r.price <= :maxPrice)")
    Page<Recipe> findByFilters(
            @Param("maxDuration") Integer maxDuration,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("category") Category category,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);

}
