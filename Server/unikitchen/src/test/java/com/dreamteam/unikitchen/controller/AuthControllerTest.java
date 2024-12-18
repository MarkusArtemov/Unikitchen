package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.AuthResponse;
import com.dreamteam.unikitchen.dto.UserInfoResponse;
import com.dreamteam.unikitchen.exception.UserAlreadyExistsException;
import com.dreamteam.unikitchen.exception.UserNotFoundException;
import com.dreamteam.unikitchen.jwt.JwtUtil;
import com.dreamteam.unikitchen.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testRegisterUser_Success() throws Exception {
        UserInfoResponse userInfoResponse = new UserInfoResponse(1L, "testuser", "This is a bio", null, null);
        when(userService.registerUser("testuser", "password123", "This is a bio")).thenReturn(userInfoResponse);

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
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.bio").value("This is a bio"));
    }

    @Test
    void testRegisterUser_UserAlreadyExists() throws Exception {
        when(userService.registerUser("testuser", "password123", "This is a bio"))
                .thenThrow(new UserAlreadyExistsException("Username 'testuser' already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "testuser",
                            "password": "password123",
                            "bio": "This is a bio"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username 'testuser' already exists"));
    }

    @Test
    void testLoginUser_Success() throws Exception {
        AuthResponse authResponse = new AuthResponse("jwt-token",
                new UserInfoResponse(1L, "testuser", "This is a bio", null, null));
        when(userService.loginUser("testuser", "password123")).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "testuser",
                            "password": "password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.userInfo.username").value("testuser"));
    }

    @Test
    void testLoginUser_InvalidCredentials() throws Exception {
        when(userService.loginUser("testuser", "wrongpassword"))
                .thenThrow(new UserNotFoundException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "testuser",
                            "password": "wrongpassword"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }
}
