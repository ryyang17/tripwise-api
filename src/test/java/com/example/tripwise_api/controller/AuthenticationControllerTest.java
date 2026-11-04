package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.request.AuthRequest;
import com.example.tripwise_api.dto.request.RegisterRequest;
import com.example.tripwise_api.dto.response.AuthResponse;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import com.example.tripwise_api.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthenticationController authenticationController;

    private AuthRequest authRequest;
    private RegisterRequest registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Set properties via reflection
        ReflectionTestUtils.setField(authenticationController, "cookieSecure", false);
        ReflectionTestUtils.setField(authenticationController, "cookieSameSite", "Lax");
        ReflectionTestUtils.setField(authenticationController, "cookieDomain", "localhost");

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("newuser"); // Match registerRequest username
        testUser.setEmail("newuser@example.com"); // Match registerRequest email
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(null);
        when(jwtUtil.generateAccessToken("testuser")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("testuser")).thenReturn("refresh-token");
        User loginUser = new User();
        loginUser.setId(42L);
        when(userService.findByUsername("testuser")).thenReturn(java.util.Optional.of(loginUser));

        // Act
        ResponseEntity<AuthResponse> result = authenticationController.login(authRequest, response);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(42L, result.getBody().getUserId());
        assertEquals("access-token", result.getBody().getAccessToken());
        assertEquals("testuser", result.getBody().getUsername());
        assertEquals("Login successful", result.getBody().getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateAccessToken("testuser");
        verify(jwtUtil, times(1)).generateRefreshToken("testuser");
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResponseEntity<AuthResponse> result = authenticationController.login(authRequest, response);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertNull(result.getBody().getAccessToken());
        assertEquals("Invalid username or password", result.getBody().getMessage());
    }

    @Test
    void register_WithValidData_ShouldReturnCreated() {
        // Arrange
        when(userService.registerUser(anyString(), anyString(), anyString())).thenReturn(testUser);
        when(jwtUtil.generateAccessToken("newuser")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("newuser")).thenReturn("refresh-token");

        // Act
        ResponseEntity<AuthResponse> result = authenticationController.register(registerRequest, response);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("access-token", result.getBody().getAccessToken());
        assertEquals("newuser", result.getBody().getUsername());
        assertEquals(1L, result.getBody().getUserId());
        verify(userService, times(1)).registerUser(
            registerRequest.getUsername(),
            registerRequest.getEmail(),
            registerRequest.getPassword()
        );
    }

    @Test
    void register_WithExistingUsername_ShouldReturnConflict() {
        // Arrange
        when(userService.registerUser(anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Username already exists"));

        // Act
        ResponseEntity<AuthResponse> result = authenticationController.register(registerRequest, response);

        // Assert
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertNotNull(result.getBody());
        assertNull(result.getBody().getAccessToken());
    }
}
