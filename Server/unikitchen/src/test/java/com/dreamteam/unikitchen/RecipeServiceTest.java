package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.RecipeCreationRequest;
import com.dreamteam.unikitchen.dto.RecipeDetailsResponse;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRecipe() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        RecipeCreationRequest request = new RecipeCreationRequest();
        request.setName("Test Recipe");
        request.setPrice(10.0);
        request.setDuration(30);
        request.setDifficultyLevel("EASY");
        request.setCategory("MAIN_COURSE");

        Recipe mockRecipe = new Recipe();
        mockRecipe.setId(1L);
        mockRecipe.setName("Test Recipe");
        mockRecipe.setPrice(10.0);
        mockRecipe.setDuration(30);
        mockRecipe.setDifficultyLevel("EASY");

        RecipeDetailsResponse mockResponse = new RecipeDetailsResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Test Recipe");
        mockResponse.setPrice(10.0);

        when(userService.getUserEntity()).thenReturn(mockUser);
        when(entityMapper.fromRecipeCreationRequestToRecipe(request, mockUser)).thenReturn(mockRecipe);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(mockRecipe);
        when(recipeService.buildRecipeDetailsResponse(mockRecipe)).thenReturn(mockResponse);

        RecipeDetailsResponse response = recipeService.createRecipe(request);

        assertEquals(mockResponse.getId(), response.getId());
        assertEquals(mockResponse.getName(), response.getName());
        assertEquals(mockResponse.getPrice(), response.getPrice());

        verify(userService, times(1)).getUserEntity();
        verify(entityMapper, times(1)).fromRecipeCreationRequestToRecipe(request, mockUser);
        verify(recipeRepository, times(1)).save(mockRecipe);
    }
}
