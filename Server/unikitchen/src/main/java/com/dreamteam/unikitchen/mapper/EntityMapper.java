package com.dreamteam.unikitchen.mapper;

import com.dreamteam.unikitchen.dto.*;
import com.dreamteam.unikitchen.model.Rating;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface EntityMapper {

    RecipeDetailsResponse toRecipeDetailsResponse(
            Recipe recipe,
            boolean isFavorite,
            Double averageRating,
            int ratingCount,
            String ownerUsername
    );

    RecipeOverviewResponse toRecipeOverviewResponse(
            Recipe recipe,
            boolean isFavorite,
            Double averageRating,
            int ratingCount
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "ratings", ignore = true)
    Recipe fromRecipeCreationRequestToRecipe(
            RecipeCreationRequest recipeCreationRequest,
            User user
    );

    @Mapping(target = "user", source = "user")
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "id", source = "recipeUpdateRequest.id")
    Recipe fromRecipeUpdateRequestToRecipe(
            RecipeUpdateRequest recipeUpdateRequest,
            User user
    );




    @Mapping(target = "id", source = "id")
    @Mapping(target = "ratingValue", source = "ratingValue")
    @Mapping(target = "userId", expression = "java(rating.getUser() != null ? rating.getUser().getId() : null)")
    @Mapping(target = "recipeId", expression = "java(rating.getRecipe() != null ? rating.getRecipe().getId() : null)")
    @Mapping(target = "createdAt", source = "createdAt")
    RatingInfo toRatingInfo(Rating rating);


    UserInfoResponse toUserInfoResponse(User user);
}
