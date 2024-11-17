package com.dreamteam.unikitchen.repository;

import com.dreamteam.unikitchen.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByUserId(Long userId);
    List<Recipe> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT r FROM Recipe r WHERE " +
            "(:maxDuration IS NULL OR r.duration <= :maxDuration) AND " +
            "(:difficultyLevel IS NULL OR r.difficultyLevel = :difficultyLevel) AND " +
            "(:category IS NULL OR r.category = :category)")
    List<Recipe> findByFilters(
            @Param("maxDuration") Integer maxDuration,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("category") String category);
}