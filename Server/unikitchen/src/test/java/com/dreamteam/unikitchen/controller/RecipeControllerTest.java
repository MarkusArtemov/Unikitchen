package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RecipeCreationRequest;
import com.dreamteam.unikitchen.dto.RecipeDetailsResponse;
import com.dreamteam.unikitchen.dto.RecipeFilterRequest;
import com.dreamteam.unikitchen.dto.RecipeOverviewResponse;
import com.dreamteam.unikitchen.dto.RecipeUpdateRequest;
import com.dreamteam.unikitchen.exception.RecipeNotFoundException;
import com.dreamteam.unikitchen.jwt.JwtUtil;
import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;
import com.dreamteam.unikitchen.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecipeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RecipeService recipeService;

    @MockBean
    JwtUtil jwtUtil;

    @Test
    void testCreateRecipe_Success() throws Exception {
        RecipeDetailsResponse mockResponse = new RecipeDetailsResponse(
                1L, "Test Recipe", 10.0, 30, DifficultyLevel.EINFACH, Category.KUCHEN,
                "Preparation",
                List.of(),
                0.0, false, 0, "testuser", 0
        );

        when(recipeService.createRecipe(any(RecipeCreationRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Test Recipe",
                            "price": 10.0,
                            "duration": 30,
                            "difficultyLevel": "EINFACH",
                            "category": "KUCHEN",
                            "preparation": "Preparation",
                            "ingredients": []
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Recipe"));
    }

    @Test
    void testCreateRecipe_MissingField() throws Exception {
        when(recipeService.createRecipe(any())).thenThrow(new IllegalArgumentException("Recipe name is required"));

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "price": 10.0,
                            "duration": 30,
                            "difficultyLevel": "EINFACH",
                            "category": "KUCHEN",
                            "preparation": "Preparation",
                            "ingredients": []
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Recipe name is required"));
    }

    @Test
    void testGetRecipeById_Success() throws Exception {
        RecipeDetailsResponse mockResponse = new RecipeDetailsResponse(
                1L, "Test Recipe", 10.0, 30, DifficultyLevel.EINFACH, Category.KUCHEN,
                "Prep",
                List.of(),
                4.5, true, 10, "testuser", 5
        );

        when(recipeService.getRecipeById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Recipe"))
                .andExpect(jsonPath("$.isFavorite").value(true));
    }

    @Test
    void testGetRecipeById_NotFound() throws Exception {
        doThrow(new RecipeNotFoundException("Recipe not found"))
                .when(recipeService).getRecipeById(999L);

        mockMvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Recipe not found"));
    }

    @Test
    void testUpdateRecipe_Success() throws Exception {
        RecipeDetailsResponse updatedResponse = new RecipeDetailsResponse(
                1L, "Updated Recipe", 15.0, 45, DifficultyLevel.MITTEL, Category.FLEISCH,
                "Updated prep",
                List.of(),
                3.5, false, 20, "testuser", 2
        );

        when(recipeService.updateRecipe(Mockito.eq(1L), any(RecipeUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "id": 1,
                            "name": "Updated Recipe",
                            "price": 15.0,
                            "duration": 45,
                            "difficultyLevel": "MITTEL",
                            "category": "FLEISCH",
                            "preparation": "Updated prep",
                            "ingredients": []
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Recipe"))
                .andExpect(jsonPath("$.price").value(15.0));
    }

    @Test
    void testUpdateRecipe_NotOwner() throws Exception {
        when(recipeService.updateRecipe(Mockito.eq(1L), any()))
                .thenThrow(new IllegalArgumentException("You are not the owner of this recipe"));

        mockMvc.perform(put("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "id": 1,
                            "name": "Updated Recipe",
                            "price": 15.0,
                            "duration": 45,
                            "difficultyLevel": "MITTEL",
                            "category": "FLEISCH",
                            "preparation": "Updated prep",
                            "ingredients": []
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You are not the owner of this recipe"));
    }

    @Test
    void testDeleteRecipe_Success() throws Exception {
        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteRecipe_NotFound() throws Exception {
        doThrow(new RecipeNotFoundException("Recipe not found"))
                .when(recipeService).deleteRecipe(999L);

        mockMvc.perform(delete("/api/recipes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Recipe not found"));
    }

    @Test
    void testGetRecentRecipes_Success() throws Exception {
        RecipeOverviewResponse r1 = new RecipeOverviewResponse(
                1L, "Latest Recipe", 12.0, 25, DifficultyLevel.EINFACH, Category.NUDELN, 4.0, 8, false);
        when(recipeService.getLast10Recipes()).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/recipes/lastRecipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Latest Recipe"));
    }

    @Test
    void testGetFilteredRecipes_Success() throws Exception {
        RecipeOverviewResponse r = new RecipeOverviewResponse(
                1L, "Filtered Recipe", 8.0, 15, DifficultyLevel.EINFACH, Category.KUCHEN, 3.5, 5, true);

        when(recipeService.getFilteredRecipes(any(RecipeFilterRequest.class)))
                .thenReturn(PageImplFactory.createPage(List.of(r)));

        mockMvc.perform(get("/api/recipes/filtered?category=KUCHEN&quick=true&cheap=true&page=0&size=5&sortBy=createdAt&direction=DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Filtered Recipe"));
    }


    static class PageImplFactory {
        static <T> org.springframework.data.domain.Page<T> createPage(List<T> content) {
            return new org.springframework.data.domain.PageImpl<>(content);
        }
    }
}
