package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthRequestTest {

    private Validator validator;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        authRequest = new AuthRequest();
    }

    @Test
    void validAuthRequest_ShouldPassValidation() {
        // Arrange
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        // Act
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(authRequest);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void setAndGetUsername_ShouldWorkCorrectly() {
        // Act
        authRequest.setUsername("testuser");

        // Assert
        assertEquals("testuser", authRequest.getUsername());
    }

    @Test
    void setAndGetPassword_ShouldWorkCorrectly() {
        // Act
        authRequest.setPassword("password123");

        // Assert
        assertEquals("password123", authRequest.getPassword());
    }

    @Test
    void emptyUsername_ShouldFailValidation() {
        // Arrange
        authRequest.setUsername("");
        authRequest.setPassword("password123");

        // Act
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(authRequest);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullUsername_ShouldFailValidation() {
        // Arrange
        authRequest.setUsername(null);
        authRequest.setPassword("password123");

        // Act
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(authRequest);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void emptyPassword_ShouldFailValidation() {
        // Arrange
        authRequest.setUsername("testuser");
        authRequest.setPassword("");

        // Act
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(authRequest);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullPassword_ShouldFailValidation() {
        // Arrange
        authRequest.setUsername("testuser");
        authRequest.setPassword(null);

        // Act
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(authRequest);

        // Assert
        assertFalse(violations.isEmpty());
    }
}

