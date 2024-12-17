package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.AuthRequest;
import com.dreamteam.unikitchen.dto.AuthResponse;
import com.dreamteam.unikitchen.dto.UserInfoResponse;
import com.dreamteam.unikitchen.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testRegisterUser() throws Exception {
        // Mock request payload
        AuthRequest authRequest = new AuthRequest("testuser", "password123", "This is a bio");

        // Mock service response
        UserInfoResponse userInfoResponse = new UserInfoResponse("testuser", "This is a bio");
        when(userService.registerUser("testuser", "password123", "This is a bio"))
                .thenReturn(userInfoResponse);

        // Perform POST request to /register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "password123",
                                    "bio": "This is a bio"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.bio", is("This is a bio")));
    }

    @Test
    void testLoginUser() throws Exception {
        // Mock request payload
        AuthRequest authRequest = new AuthRequest("testuser", "password123", null);

        // Mock service response
        AuthResponse authResponse = new AuthResponse(
                "jwt-token",
                new UserInfoResponse("testuser", "This is a bio")
        );
        when(userService.loginUser("testuser", "password123")).thenReturn(authResponse);

        // Perform POST request to /login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("jwt-token")))
                .andExpect(jsonPath("$.userInfo.username", is("testuser")))
                .andExpect(jsonPath("$.userInfo.bio", is("This is a bio")));
    }
}
