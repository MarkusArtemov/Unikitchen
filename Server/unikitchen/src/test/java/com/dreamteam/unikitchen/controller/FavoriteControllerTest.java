package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RecipeOverviewResponse;
import com.dreamteam.unikitchen.exception.RecipeNotFoundException;
import com.dreamteam.unikitchen.service.FavoriteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dreamteam.unikitchen.exception.ApiExceptionHandler;
import com.dreamteam.unikitchen.jwt.JwtUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoriteController.class)
@Import(ApiExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService favoriteService;

    @MockBean
    private JwtUtil jwtUtil; // Mocking JwtUtil, um Abhängigkeiten zu erfüllen

    @Nested
    @DisplayName("Toggle Favorite Tests")
    class ToggleFavoriteTests {

        @Test
        @DisplayName("Successfully add a recipe to favorites")
        void toggleFavorite_AddToFavorites_Success() throws Exception {
            Long recipeId = 1L;
            when(favoriteService.toggleFavorite(recipeId)).thenReturn(true);

            mockMvc.perform(put("/api/favorites/toggle/{recipeId}", recipeId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isFavorite").value(true));

            verify(favoriteService, times(1)).toggleFavorite(recipeId);
        }

        @Test
        @DisplayName("Successfully remove a recipe from favorites")
        void toggleFavorite_RemoveFromFavorites_Success() throws Exception {
            Long recipeId = 2L;
            when(favoriteService.toggleFavorite(recipeId)).thenReturn(false);

            mockMvc.perform(put("/api/favorites/toggle/{recipeId}", recipeId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isFavorite").value(false));

            verify(favoriteService, times(1)).toggleFavorite(recipeId);
        }

        @Test
        @DisplayName("Toggle favorite with non-existent recipe should return 404")
        void toggleFavorite_RecipeNotFound_Returns404() throws Exception {
            Long recipeId = 999L;
            when(favoriteService.toggleFavorite(recipeId))
                    .thenThrow(new RecipeNotFoundException("Recipe not found with id: " + recipeId));

            mockMvc.perform(put("/api/favorites/toggle/{recipeId}", recipeId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Recipe not found")));

            verify(favoriteService, times(1)).toggleFavorite(recipeId);
        }
    }

    @Nested
    @DisplayName("Get Favorites Tests")
    class GetFavoritesTests {

        @Test
        @DisplayName("Successfully retrieve list of favorite recipes")
        void getFavorites_Success() throws Exception {
            RecipeOverviewResponse recipe1 = new RecipeOverviewResponse(
                    1L, "Spaghetti Bolognese", 12.99, 30,
                    com.dreamteam.unikitchen.model.enums.DifficultyLevel.MITTEL,
                    com.dreamteam.unikitchen.model.enums.Category.FLEISCH,
                    4.5, 10, true
            );

            RecipeOverviewResponse recipe2 = new RecipeOverviewResponse(
                    2L, "Caesar Salad", 8.99, 15,
                    com.dreamteam.unikitchen.model.enums.DifficultyLevel.EINFACH,
                    com.dreamteam.unikitchen.model.enums.Category.VEGETARISCH,
                    4.0, 8, true
            );

            List<RecipeOverviewResponse> favorites = Arrays.asList(recipe1, recipe2);
            when(favoriteService.getFavoritesByUser()).thenReturn(favorites);

            mockMvc.perform(get("/api/favorites/current")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("Spaghetti Bolognese"))
                    .andExpect(jsonPath("$[0].difficultyLevel").value("MITTEL"))
                    .andExpect(jsonPath("$[0].category").value("FLEISCH"))
                    .andExpect(jsonPath("$[0].isFavorite").value(true))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].name").value("Caesar Salad"))
                    .andExpect(jsonPath("$[1].difficultyLevel").value("EINFACH"))
                    .andExpect(jsonPath("$[1].category").value("VEGETARISCH"))
                    .andExpect(jsonPath("$[1].isFavorite").value(true));

            verify(favoriteService, times(1)).getFavoritesByUser();
        }

        @Test
        @DisplayName("Retrieve favorites when user has no favorites returns empty list")
        void getFavorites_NoFavorites_ReturnsEmptyList() throws Exception {
            when(favoriteService.getFavoritesByUser()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/favorites/current")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(favoriteService, times(1)).getFavoritesByUser();
        }

        @Test
        @DisplayName("Get favorites when service throws exception returns appropriate error")
        void getFavorites_ServiceThrowsException_ReturnsError() throws Exception {
            when(favoriteService.getFavoritesByUser())
                    .thenThrow(new IllegalArgumentException("Invalid user"));

            mockMvc.perform(get("/api/favorites/current")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("Illegal Argument")))
                    .andExpect(jsonPath("$.message", is("Invalid user")));

            verify(favoriteService, times(1)).getFavoritesByUser();
        }
    }

    @Nested
    @DisplayName("Exception Handling Tests")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Handle generic exception gracefully")
        void handleGenericException_ReturnsInternalServerError() throws Exception {
            Long recipeId = 3L;
            when(favoriteService.toggleFavorite(recipeId))
                    .thenThrow(new RuntimeException("Unexpected error"));

            mockMvc.perform(put("/api/favorites/toggle/{recipeId}", recipeId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error", is("Internal Server Error")))
                    .andExpect(jsonPath("$.message", is("Unexpected error")));

            verify(favoriteService, times(1)).toggleFavorite(recipeId);
        }

        @Test
        @DisplayName("Handle MethodArgumentTypeMismatchException when recipeId is not a number")
        void handleMethodArgumentTypeMismatchException_InvalidRecipeId() throws Exception {
            String invalidRecipeId = "abc";

            mockMvc.perform(put("/api/favorites/toggle/{recipeId}", invalidRecipeId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("Type Mismatch")))
                    .andExpect(jsonPath("$.message", containsString("should be of type 'Long'")));
        }

        @Test
        @DisplayName("Handle MissingServletRequestParameterException")
        void handleMissingServletRequestParameterException_MissingParameter() throws Exception {
            // Da die aktuellen Controller-Methoden keine zusätzlichen Request-Parameter erwarten,
            // ist dieser Test möglicherweise nicht relevant. Falls zukünftige Endpunkte Parameter erwarten,
            // kann dieser Test entsprechend angepasst werden.
            mockMvc.perform(get("/api/favorites/current")
                            .param("extraParam", "") // Beispiel für einen unerwarteten Parameter
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()); // Aktuell wird ein zusätzlicher Parameter ignoriert
        }
    }
}
