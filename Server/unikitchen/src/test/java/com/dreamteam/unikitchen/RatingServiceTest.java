import com.dreamteam.unikitchen.context.CurrentUserContext;
import com.dreamteam.unikitchen.dto.RatingInfo;
import com.dreamteam.unikitchen.exception.RatingNotFoundException;
import com.dreamteam.unikitchen.exception.RecipeNotFoundException;
import com.dreamteam.unikitchen.exception.UserNotFoundException;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.Rating;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.RatingRepository;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrUpdateRating_CreatesNewRating() {
        // Arrange
        Long recipeId = 1L;
        int ratingValue = 5;
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Rating newRating = new Rating();
        newRating.setRatingValue(ratingValue);

        when(CurrentUserContext.getCurrentUsername()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(newRating);
        when(entityMapper.toRatingInfo(any(Rating.class))).thenReturn(new RatingInfo(ratingValue));

        RatingInfo result = ratingService.createOrUpdateRating(recipeId, ratingValue);

        assertEquals(ratingValue, result.getValue());
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void testCreateOrUpdateRating_UpdatesExistingRating() {
        // Arrange
        Long recipeId = 1L;
        int newValue = 4;
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Rating existingRating = new Rating();
        existingRating.setRatingValue(5);

        when(CurrentUserContext.getCurrentUsername()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(existingRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(existingRating);
        when(entityMapper.toRatingInfo(any(Rating.class))).thenReturn(new RatingInfo(newValue));

        RatingInfo result = ratingService.createOrUpdateRating(recipeId, newValue);

        assertEquals(newValue, result.getValue());
        verify(ratingRepository).save(existingRating);
    }

    @Test
    void testGetRatingsByRecipe() {
        // Arrange
        Long recipeId = 1L;
        Rating rating = new Rating();
        RatingInfo ratingInfo = new RatingInfo(5);

        when(ratingRepository.findByRecipeId(recipeId)).thenReturn(List.of(rating));
        when(entityMapper.toRatingInfo(rating)).thenReturn(ratingInfo);

        List<RatingInfo> result = ratingService.getRatingsByRecipe(recipeId);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getValue());
    }

    @Test
    void testDeleteRating_OwnerDeletesRating() {
        // Arrange
        Long ratingId = 1L;
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        Rating rating = new Rating();
        rating.setUser(user);

        when(CurrentUserContext.getCurrentUsername()).thenReturn(username);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        assertDoesNotThrow(() -> ratingService.deleteRating(ratingId));

        verify(ratingRepository).delete(rating);
    }

    @Test
    void testDeleteRating_NotOwnerThrowsException() {
        // Arrange
        Long ratingId = 1L;
        String username = "testUser";
        User user = new User();
        user.setUsername("otherUser");

        Rating rating = new Rating();
        rating.setUser(user);

        when(CurrentUserContext.getCurrentUsername()).thenReturn(username);
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ratingService.deleteRating(ratingId));
        assertEquals("You are not the owner of this rating", exception.getMessage());
    }

    @Test
    void testCalculateAverageRating() {
        // Arrange
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
        // Arrange
        Long recipeId = 1L;
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Rating rating = new Rating();
        rating.setRatingValue(5);

        when(CurrentUserContext.getCurrentUsername()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(rating));
        when(entityMapper.toRatingInfo(rating)).thenReturn(new RatingInfo(5));

        RatingInfo result = ratingService.getUserRating(recipeId);

        assertEquals(5, result.getValue());
    }
}
