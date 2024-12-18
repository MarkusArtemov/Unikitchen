package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.RatingInfo;
import com.dreamteam.unikitchen.exception.RecipeNotFoundException;
import com.dreamteam.unikitchen.jwt.JwtUtil;
import com.dreamteam.unikitchen.service.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@AutoConfigureMockMvc(addFilters = false)
class RatingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RatingService ratingService;

    @MockBean
    JwtUtil jwtUtil;

    @Test
    void testCreateOrUpdateRating_Success() throws Exception {
        RatingInfo ratingInfo = new RatingInfo(1L, 5, 10L, 1L, null);
        when(ratingService.createOrUpdateRating(1L, 5)).thenReturn(ratingInfo);

        mockMvc.perform(post("/api/ratings/recipe/1?ratingValue=5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ratingValue").value(5));
    }

    @Test
    void testCreateOrUpdateRating_RecipeNotFound() throws Exception {
        doThrow(new RecipeNotFoundException("Recipe not found"))
                .when(ratingService).createOrUpdateRating(999L, 5);

        mockMvc.perform(post("/api/ratings/recipe/999?ratingValue=5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Recipe not found"));
    }

    @Test
    void testGetRatingsByRecipe_Success() throws Exception {
        RatingInfo r1 = new RatingInfo(1L,4,10L,1L,null);
        when(ratingService.getRatingsByRecipe(1L)).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/ratings/recipe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ratingValue").value(4));
    }
}
