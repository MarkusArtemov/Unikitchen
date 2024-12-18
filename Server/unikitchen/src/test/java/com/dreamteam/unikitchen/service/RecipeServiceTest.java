package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.IngredientInfo;
import com.dreamteam.unikitchen.dto.RecipeCreationRequest;
import com.dreamteam.unikitchen.dto.RecipeDetailsResponse;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.Recipe;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;
import com.dreamteam.unikitchen.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecipeServiceTest {


}
