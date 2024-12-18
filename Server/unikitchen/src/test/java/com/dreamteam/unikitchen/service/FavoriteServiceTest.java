package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.RecipeOverviewResponse;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.Favorite;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;
import com.dreamteam.unikitchen.repository.FavoriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteServiceTest {

    private FavoriteService favoriteService;
    private FavoriteRepository favoriteRepository;
    private UserService userService;
    private RatingService ratingService;
    private EntityMapper entityMapper;

    @BeforeEach
    void setUp() {
        favoriteRepository = mock(FavoriteRepository.class);
        userService = mock(UserService.class);
        ratingService = mock(RatingService.class);
        entityMapper = mock(EntityMapper.class);

        favoriteService = new FavoriteService(favoriteRepository, userService, ratingService, entityMapper);
    }

    @Test
    void testToggleFavorite_AddsFavoriteIfNotExists() {
        Long recipeId = 1L;
        User user = new User();
        user.setId(2L);
        user.setUsername("test@example.com");

        when(userService.getUserEntity()).thenReturn(user);
        when(favoriteRepository.findByUserIdAndRecipeId(user.getId(), recipeId)).thenReturn(null);
        // Nach dem Hinzufügen wird isFavorite auf true geprüft
        when(favoriteRepository.existsByUserIdAndRecipeId(user.getId(), recipeId)).thenReturn(true);

        boolean result = favoriteService.toggleFavorite(recipeId);

        assertTrue(result);
        ArgumentCaptor<Favorite> captor = ArgumentCaptor.forClass(Favorite.class);
        verify(favoriteRepository).save(captor.capture());
        assertEquals(user, captor.getValue().getUser());
        assertEquals(recipeId, captor.getValue().getRecipe().getId());
    }

    @Test
    void testToggleFavorite_RemovesFavoriteIfExists() {
        Long recipeId = 1L;
        User user = new User();
        user.setId(2L);
        user.setUsername("test@example.com");
        Favorite existingFavorite = new Favorite(1L, user, new Recipe(recipeId));

        when(userService.getUserEntity()).thenReturn(user);
        when(favoriteRepository.findByUserIdAndRecipeId(user.getId(), recipeId)).thenReturn(existingFavorite);
        when(favoriteRepository.existsByUserIdAndRecipeId(user.getId(), recipeId)).thenReturn(false);

        boolean result = favoriteService.toggleFavorite(recipeId);

        assertFalse(result);
        verify(favoriteRepository).deleteByUserIdAndRecipeId(user.getId(), recipeId);
    }

    @Test
    void testGetFavoritesByUser_ReturnsMappedFavorites() {
        User user = new User();
        user.setId(2L);
        user.setUsername("test@example.com");

        Favorite favorite1 = new Favorite(1L, user, new Recipe(1L));
        Favorite favorite2 = new Favorite(2L, user, new Recipe(2L));

        when(userService.getUserEntity()).thenReturn(user);
        when(favoriteRepository.findByUserId(user.getId())).thenReturn(List.of(favorite1, favorite2));

        when(ratingService.calculateAverageRating(1L)).thenReturn(4.5);
        when(ratingService.getRatingCount(1L)).thenReturn(10);
        when(ratingService.calculateAverageRating(2L)).thenReturn(3.8);
        when(ratingService.getRatingCount(2L)).thenReturn(5);

        RecipeOverviewResponse response1 = new RecipeOverviewResponse(
                1L, "Recipe 1", 5.0, 20, DifficultyLevel.EINFACH, Category.KUCHEN, 4.5, 10, true);
        RecipeOverviewResponse response2 = new RecipeOverviewResponse(
                2L, "Recipe 2", 7.0, 40, DifficultyLevel.MITTEL, Category.FLEISCH, 3.8, 5, true);

        when(entityMapper.toRecipeOverviewResponse(any(Recipe.class), eq(true), eq(4.5), eq(10))).thenReturn(response1);
        when(entityMapper.toRecipeOverviewResponse(any(Recipe.class), eq(true), eq(3.8), eq(5))).thenReturn(response2);

        List<RecipeOverviewResponse> result = favoriteService.getFavoritesByUser();

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));
    }

    @Test
    void testIsFavorite_ReturnsTrueIfExists() {
        Long recipeId = 1L;
        User user = new User();
        user.setId(2L);
        user.setUsername("test@example.com");

        when(userService.getUserEntity()).thenReturn(user);
        when(favoriteRepository.existsByUserIdAndRecipeId(user.getId(), recipeId)).thenReturn(true);

        boolean result = favoriteService.isFavorite(recipeId);

        assertTrue(result);
    }

    @Test
    void testIsFavorite_ReturnsFalseIfNotExists() {
        Long recipeId = 1L;
        User user = new User();
        user.setId(2L);
        user.setUsername("test@example.com");

        when(userService.getUserEntity()).thenReturn(user);
        when(favoriteRepository.existsByUserIdAndRecipeId(user.getId(), recipeId)).thenReturn(false);

        boolean result = favoriteService.isFavorite(recipeId);

        assertFalse(result);
    }

    @Test
    void testDeleteFavoritesByRecipeId() {
        Long recipeId = 1L;

        favoriteService.deleteFavoritesByRecipeId(recipeId);

        verify(favoriteRepository).deleteByRecipeId(recipeId);
    }
}
