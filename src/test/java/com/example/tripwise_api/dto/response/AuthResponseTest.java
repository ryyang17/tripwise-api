package com.example.tripwise_api.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void constructorWithParameters_ShouldSetAllFields() {
        // Act
        AuthResponse response = new AuthResponse("access-token", "testuser", "Login successful");

        // Assert
        assertEquals("access-token", response.getAccessToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void setAndGetAccessToken_ShouldWorkCorrectly() {
        // Arrange
        AuthResponse authResponse = new AuthResponse("token", "user", "msg");

        // Act
        authResponse.setAccessToken("test-token");

        // Assert
        assertEquals("test-token", authResponse.getAccessToken());
    }

    @Test
    void setAndGetUsername_ShouldWorkCorrectly() {
        // Arrange
        AuthResponse authResponse = new AuthResponse("token", "user", "msg");

        // Act
        authResponse.setUsername("testuser");

        // Assert
        assertEquals("testuser", authResponse.getUsername());
    }

    @Test
    void setAndGetMessage_ShouldWorkCorrectly() {
        // Arrange
        AuthResponse authResponse = new AuthResponse("token", "user", "msg");

        // Act
        authResponse.setMessage("Success");

        // Assert
        assertEquals("Success", authResponse.getMessage());
    }
}

