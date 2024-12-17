package com.dreamteam.unikitchen.service;

import com.dreamteam.unikitchen.dto.UserInfoResponse;
import com.dreamteam.unikitchen.exception.UserAlreadyExistsException;
import com.dreamteam.unikitchen.mapper.EntityMapper;
import com.dreamteam.unikitchen.model.User;
import com.dreamteam.unikitchen.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String bio = "Test bio";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword");
        mockUser.setBio(bio);

        UserInfoResponse mockResponse = new UserInfoResponse(username, bio, null);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(entityMapper.toUserInfoResponse(mockUser)).thenReturn(mockResponse);

        UserInfoResponse response = userService.registerUser(username, password, bio);

        assertEquals(username, response.username());
        assertEquals(bio, response.bio());

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
        verify(entityMapper, times(1)).toUserInfoResponse(mockUser);
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String bio = "Test bio";

        User existingUser = new User();
        existingUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.registerUser(username, password, bio)
        );

        assertEquals("Username 'testuser' already exists", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
