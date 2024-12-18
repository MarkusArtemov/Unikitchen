package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.context.CurrentUserContext;
import com.dreamteam.unikitchen.dto.RatingInfo;
import com.dreamteam.unikitchen.exception.RecipeNotFoundException;
import com.dreamteam.unikitchen.exception.UserNotFoundException;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.Rating;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.RatingRepository;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private RatingService ratingService;

    private MockedStatic<CurrentUserContext> mockedContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // MockedStatic für CurrentUserContext
        mockedContext = mockStatic(CurrentUserContext.class);
    }

    @AfterEach
    void tearDown() {
        mockedContext.close();
    }

    @Test
    void testCreateOrUpdateRating_CreatesNewRating() {
        Long recipeId = 1L;
        int ratingValue = 5;
        String username = "testUser";

        User user = new User();
        user.setUsername(username);
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.empty());

        Rating newRating = new Rating();
        newRating.setRatingValue(ratingValue);

        when(ratingRepository.save(any(Rating.class))).thenReturn(newRating);

        RatingInfo mappedInfo = new RatingInfo(null, ratingValue, null, recipeId, LocalDateTime.now());
        when(entityMapper.toRatingInfo(any(Rating.class))).thenReturn(mappedInfo);

        RatingInfo result = ratingService.createOrUpdateRating(recipeId, ratingValue);
        assertEquals(ratingValue, result.ratingValue());
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void testCreateOrUpdateRating_UpdatesExistingRating() {
        Long recipeId = 1L;
        int newValue = 4;
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Rating existingRating = new Rating();
        existingRating.setRatingValue(5);

        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(existingRating));

        Rating updatedRating = new Rating();
        updatedRating.setRatingValue(newValue);

        when(ratingRepository.save(existingRating)).thenReturn(updatedRating);
        RatingInfo mappedInfo = new RatingInfo(null, newValue, null, recipeId, LocalDateTime.now());
        when(entityMapper.toRatingInfo(updatedRating)).thenReturn(mappedInfo);

        RatingInfo result = ratingService.createOrUpdateRating(recipeId, newValue);
        assertEquals(newValue, result.ratingValue());
        verify(ratingRepository).save(existingRating);
    }

    @Test
    void testGetRatingsByRecipe() {
        Long recipeId = 1L;
        Rating rating = new Rating();
        rating.setRatingValue(5);

        when(ratingRepository.findByRecipeId(recipeId)).thenReturn(List.of(rating));
        RatingInfo ratingInfo = new RatingInfo(null, 5, null, recipeId, LocalDateTime.now());
        when(entityMapper.toRatingInfo(rating)).thenReturn(ratingInfo);

        List<RatingInfo> result = ratingService.getRatingsByRecipe(recipeId);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).ratingValue());
    }

    @Test
    void testDeleteRating_OwnerDeletesRating() {
        Long ratingId = 1L;
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        Rating rating = new Rating();
        rating.setUser(user);

        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        assertDoesNotThrow(() -> ratingService.deleteRating(ratingId));
        verify(ratingRepository).delete(rating);
    }

    @Test
    void testDeleteRating_NotOwnerThrowsException() {
        Long ratingId = 1L;
        String username = "testUser";
        User user = new User();
        user.setUsername("otherUser");

        Rating rating = new Rating();
        rating.setUser(user);

        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ratingService.deleteRating(ratingId));
        assertEquals("You are not the owner of this rating", exception.getMessage());
    }

    @Test
    void testCalculateAverageRating() {
        Long recipeId = 1L;
        Rating rating1 = new Rating();
        rating1.setRatingValue(4);
        Rating rating2 = new Rating();
        rating2.setRatingValue(5);

        when(ratingRepository.findByRecipeId(recipeId)).thenReturn(List.of(rating1, rating2));

        Double result = ratingService.calculateAverageRating(recipeId);
        assertEquals(4.5, result);
    }

    @Test
    void testGetUserRating_RatingExists() {
        Long recipeId = 1L;
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Rating rating = new Rating();
        rating.setRatingValue(5);

        mockedContext.when(CurrentUserContext::getCurrentUsername).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(rating));

        RatingInfo ratingInfo = new RatingInfo(null, 5, null, recipeId, LocalDateTime.now());
        when(entityMapper.toRatingInfo(rating)).thenReturn(ratingInfo);

        RatingInfo result = ratingService.getUserRating(recipeId);
        assertEquals(5, result.ratingValue());
    }
}
