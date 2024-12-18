package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RecipeDetailsResponse;
import com.dreamteam.unikitchen.model.enums.Category;
import com.dreamteam.unikitchen.model.enums.DifficultyLevel;
import com.dreamteam.unikitchen.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

}

