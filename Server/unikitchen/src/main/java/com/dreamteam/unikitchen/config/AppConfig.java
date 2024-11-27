package com.dreamteam.unikitchen.config;

import com.dreamteam.unikitchen.repository.RecipeRepository;
import com.dreamteam.unikitchen.repository.UserRepository;
import com.dreamteam.unikitchen.service.RecipeService;
import com.dreamteam.unikitchen.service.RecipeServiceInterface;
import com.dreamteam.unikitchen.service.RecipeValidatorDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public RecipeService recipeService(RecipeRepository recipeRepository, UserRepository userRepository) {
        return new RecipeService(recipeRepository, userRepository);
    }

    @Bean
    public RecipeServiceInterface decoratedRecipeService(RecipeService recipeService) {
        return new RecipeValidatorDecorator(recipeService);
    }
}

