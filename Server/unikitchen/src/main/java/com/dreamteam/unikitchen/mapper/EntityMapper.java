package com.dreamteam.unikitchen.mapper;

import com.dreamteam.unikitchen.dto.*;
import com.dreamteam.unikitchen.model.Rating;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    // Converts a Recipe to RecipeDetailsResponse
    RecipeDetailsResponse toRecipeDetailsResponse(
            Recipe recipe,
            boolean isFavorite,
            Double averageRating,
            int ratingCount,
            String ownerUsername
    );

    // Converts a Recipe to RecipeOverviewResponse
    RecipeOverviewResponse toRecipeOverviewResponse(
            Recipe recipe,
            boolean isFavorite,
            Double averageRating,
            int ratingCount
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "ratings", ignore = true)
        // Converts RecipeCreationRequest to a new Recipe
    Recipe fromRecipeCreationRequestToRecipe(
            RecipeCreationRequest recipeCreationRequest,
            User user
    );

    @Mapping(target = "user", source = "user")
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "id", source = "recipeUpdateRequest.id")
        // Converts RecipeUpdateRequest to an existing Recipe
    Recipe fromRecipeUpdateRequestToRecipe(
            RecipeUpdateRequest recipeUpdateRequest,
            User user
    );

    @Mapping(target = "id", source = "id")
    @Mapping(target = "ratingValue", source = "ratingValue")
    @Mapping(target = "userId", expression = "java(rating.getUser() != null ? rating.getUser().getId() : null)")
    @Mapping(target = "recipeId", expression = "java(rating.getRecipe() != null ? rating.getRecipe().getId() : null)")
    @Mapping(target = "createdAt", source = "createdAt")
        // Converts a Rating to RatingInfo
    RatingInfo toRatingInfo(Rating rating);

    // Converts a User to UserInfoResponse
    UserInfoResponse toUserInfoResponse(User user);
}
