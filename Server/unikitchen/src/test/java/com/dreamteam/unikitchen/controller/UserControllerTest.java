package com.dreamteam.unikitchen.controller;

import com.dreamteam.unikitchen.dto.UserInfoResponse;
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

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    JwtUtil jwtUtil;

    @Test
    void testGetCurrentUser_Success() throws Exception {
        UserInfoResponse userInfo = new UserInfoResponse(1L, "testuser", "Bio", null, null);
        when(userService.getUserInfo()).thenReturn(userInfo);

        mockMvc.perform(get("/api/users/current-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testUpdateCurrentUser_Success() throws Exception {
        when(userService.updateUserProfile(any())).thenReturn("User bio updated successfully");

        mockMvc.perform(put("/api/users/current-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                           "id":1,
                           "username":"testuser",
                           "bio":"New bio"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("User bio updated successfully"));
    }

    @Test
    void testUpdateCurrentUser_UserNotFound() throws Exception {
        when(userService.updateUserProfile(any()))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/api/users/current-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                           "id":1,
                           "username":"unknown",
                           "bio":"New bio"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testUploadProfileImage_Success() throws Exception {
        when(userService.uploadProfileImage(any())).thenReturn("/path/to/image.jpg");

        mockMvc.perform(multipart("/api/users/current/profile-image")
                        .file("image","dummycontent".getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile image uploaded successfully: /path/to/image.jpg"));
    }

    @Test
    void testUploadProfileImage_IOError() throws Exception {
        when(userService.uploadProfileImage(any())).thenThrow(new IOException("Disk full"));

        mockMvc.perform(multipart("/api/users/current/profile-image")
                        .file("image","dummycontent".getBytes()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error while saving the image"));
    }
}
