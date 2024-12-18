package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.context.CurrentUserContext;
import com.dreamteam.unikitchen.dto.*;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private RatingService ratingService;

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private RecipeService recipeService;

    private MockedStatic<CurrentUserContext> mockedContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedContext = mockStatic(CurrentUserContext.class);
    }

    @AfterEach
    void tearDown() {
        mockedContext.close();
    }

    @Test
    void testCreateRecipe_Success() {
        String username = "testUser";
        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        when(userService.getUserEntity()).thenReturn(user);

        RecipeCreationRequest request = new RecipeCreationRequest(
                "New Recipe", 10.0, 30, DifficultyLevel.EINFACH, Category.KUCHEN, "Prep", List.of()
        );
        Recipe recipe = new Recipe();
        recipe.setName("New Recipe");
        recipe.setPrice(10.0);
        recipe.setDuration(30);
        recipe.setDifficultyLevel(DifficultyLevel.EINFACH);
        recipe.setCategory(Category.KUCHEN);
        recipe.setUser(user);
        recipe.setPreparation("Prep");

        when(entityMapper.fromRecipeCreationRequestToRecipe(request, user)).thenReturn(recipe);
        when(recipeRepository.save(recipe)).thenReturn(recipe);

        RecipeDetailsResponse response = new RecipeDetailsResponse(
                1L,"New Recipe",10.0,30,DifficultyLevel.EINFACH,Category.KUCHEN,"Prep",List.of(),0.0,false,0,username,0
        );
        when(favoriteService.isFavorite(anyLong())).thenReturn(false);
        when(ratingService.calculateAverageRating(anyLong())).thenReturn(0.0);
        when(ratingService.getRatingCount(anyLong())).thenReturn(0);
        when(entityMapper.toRecipeDetailsResponse(eq(recipe), eq(false), eq(0.0), eq(0), eq(username))).thenReturn(response);

        RecipeDetailsResponse result = recipeService.createRecipe(request);
        assertEquals("New Recipe", result.name());
        assertEquals(10.0, result.price());
    }

    @Test
    void testCreateRecipe_ValidationError() {
        String username = "testUser";
        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        when(userService.getUserEntity()).thenReturn(user);

        RecipeCreationRequest request = new RecipeCreationRequest(
                null, 10.0, 30, DifficultyLevel.EINFACH, Category.KUCHEN, "Prep", List.of()
        );

        Recipe invalidRecipe = new Recipe();
        invalidRecipe.setName(null);
        invalidRecipe.setPrice(10.0);
        invalidRecipe.setDuration(30);
        invalidRecipe.setDifficultyLevel(DifficultyLevel.EINFACH);
        invalidRecipe.setCategory(Category.KUCHEN);
        invalidRecipe.setUser(user);
        invalidRecipe.setPreparation("Prep");

        when(entityMapper.fromRecipeCreationRequestToRecipe(request, user)).thenReturn(invalidRecipe);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> recipeService.createRecipe(request));
        assertEquals("Recipe name is required", ex.getMessage());
    }

    @Test
    void testUpdateRecipe_Success() {
        String username = "testUser";
        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);

        User user = new User();
        user.setUsername(username);

        Recipe existing = new Recipe();
        existing.setId(1L);
        existing.setUser(user);
        existing.setName("Old Name");
        existing.setPrice(5.0);
        existing.setDuration(20);
        existing.setDifficultyLevel(DifficultyLevel.EINFACH);
        existing.setCategory(Category.KUCHEN);
        existing.setPreparation("Old prep");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(existing));

        RecipeUpdateRequest updateRequest = new RecipeUpdateRequest(
                1L,"Updated",12.0,25,DifficultyLevel.MITTEL,Category.FLEISCH,"New prep",List.of()
        );

        Recipe updated = new Recipe();
        updated.setName("Updated");
        updated.setPrice(12.0);
        updated.setDuration(25);
        updated.setDifficultyLevel(DifficultyLevel.MITTEL);
        updated.setCategory(Category.FLEISCH);
        updated.setUser(user);
        updated.setPreparation("New prep");

        when(entityMapper.fromRecipeUpdateRequestToRecipe(updateRequest, user)).thenReturn(updated);
        when(recipeRepository.save(updated)).thenReturn(updated);

        RecipeDetailsResponse response = new RecipeDetailsResponse(
                1L,"Updated",12.0,25,DifficultyLevel.MITTEL,Category.FLEISCH,"New prep",List.of(),4.0,false,10,username,2
        );
        when(favoriteService.isFavorite(anyLong())).thenReturn(false);
        when(ratingService.calculateAverageRating(anyLong())).thenReturn(4.0);
        when(ratingService.getRatingCount(anyLong())).thenReturn(2);

        when(entityMapper.toRecipeDetailsResponse(
                any(Recipe.class),
                anyBoolean(),
                anyDouble(),
                anyInt(),
                eq(username))
        ).thenReturn(response);

        RecipeDetailsResponse result = recipeService.updateRecipe(1L, updateRequest);
        assertNotNull(result);
        assertEquals("Updated", result.name());
        assertEquals(12.0, result.price());
    }

    @Test
    void testUpdateRecipe_NotOwner() {
        String username = "testUser";
        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);

        User otherUser = new User();
        otherUser.setUsername("someoneElse");

        Recipe existing = new Recipe();
        existing.setId(1L);
        existing.setUser(otherUser);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(existing));

        RecipeUpdateRequest updateRequest = new RecipeUpdateRequest(
                1L,"Updated",12.0,25,DifficultyLevel.MITTEL,Category.FLEISCH,"New prep",List.of()
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> recipeService.updateRecipe(1L, updateRequest));
        assertEquals("You are not the owner of this recipe", ex.getMessage());
    }

    @Test
    void testDeleteRecipe_Success() {
        String username = "testUser";
        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);

        User user = new User();
        user.setUsername(username);

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setUser(user);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        assertDoesNotThrow(() -> recipeService.deleteRecipe(1L));
        verify(favoriteService).deleteFavoritesByRecipeId(1L);
        verify(ratingService).deleteRatingsByRecipeId(1L);
        verify(imageService).deleteImage(null);
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void testDeleteRecipe_NotOwner() {
        String username = "testUser";
        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);

        User otherUser = new User();
        otherUser.setUsername("someoneElse");

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setUser(otherUser);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> recipeService.deleteRecipe(1L));
        assertEquals("You are not the owner of this recipe", ex.getMessage());
    }

    @Test
    void testGetRecipeById_Success() {
        String username = "testUser";
        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);

        User user = new User();
        user.setUsername(username);

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("My Recipe");
        recipe.setUser(user);
        recipe.setPrice(10.0);
        recipe.setDuration(20);
        recipe.setDifficultyLevel(DifficultyLevel.EINFACH);
        recipe.setCategory(Category.KUCHEN);
        recipe.setPreparation("Prep");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        RecipeDetailsResponse response = new RecipeDetailsResponse(
                1L,"My Recipe",10.0,20,DifficultyLevel.EINFACH,Category.KUCHEN,"Prep",List.of(),0.0,false,0,username,0
        );
        when(favoriteService.isFavorite(1L)).thenReturn(false);
        when(ratingService.calculateAverageRating(1L)).thenReturn(0.0);
        when(ratingService.getRatingCount(1L)).thenReturn(0);
        when(entityMapper.toRecipeDetailsResponse(eq(recipe),eq(false),eq(0.0),eq(0),eq(username))).thenReturn(response);

        RecipeDetailsResponse result = recipeService.getRecipeById(1L);
        assertEquals("My Recipe", result.name());
    }

    @Test
    void testGetRecipeById_NotFound() {
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> recipeService.getRecipeById(999L));
        assertEquals("Recipe not found", ex.getMessage());
    }

    @Test
    void testUploadRecipeImage_Success() throws IOException {
        String username = "testUser";
        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);

        User user = new User();
        user.setUsername(username);

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setUser(user);
        recipe.setName("Has Image");
        recipe.setPrice(10.0);
        recipe.setDuration(20);
        recipe.setDifficultyLevel(DifficultyLevel.EINFACH);
        recipe.setCategory(Category.KUCHEN);
        recipe.setPreparation("Prep");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        MultipartFile file = mock(MultipartFile.class);
        when(imageService.saveImage(file)).thenReturn("/path/to/image.jpg");

        assertDoesNotThrow(() -> recipeService.uploadRecipeImage(1L, file));

        assertEquals("/path/to/image.jpg", recipe.getRecipeImagePath());
        verify(recipeRepository).save(recipe);
    }

    @Test
    void testUploadRecipeImage_NotOwner() throws IOException {
        String username = "testUser";
        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);

        User otherUser = new User();
        otherUser.setUsername("someoneElse");

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setUser(otherUser);
        recipe.setName("Has Image");
        recipe.setPrice(10.0);
        recipe.setDuration(20);
        recipe.setDifficultyLevel(DifficultyLevel.EINFACH);
        recipe.setCategory(Category.KUCHEN);
        recipe.setPreparation("Prep");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        MultipartFile file = mock(MultipartFile.class);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> recipeService.uploadRecipeImage(1L, file));
        assertEquals("You are not the owner of this recipe", ex.getMessage());
    }

    @Test
    void testGetFilteredRecipes() {
        RecipeFilterRequest filter = new RecipeFilterRequest("kuchen",false,false,null,0,10,"createdAt","DESC");

        when(recipeRepository.findByFilters(any(), any(), any(), any(), any())).thenReturn(Page.empty());

        Page<RecipeOverviewResponse> result = recipeService.getFilteredRecipes(filter);
        assertTrue(result.isEmpty());
    }
}
