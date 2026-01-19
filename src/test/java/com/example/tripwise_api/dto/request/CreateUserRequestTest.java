package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateUserRequestTest {

    private Validator validator;
    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new CreateUserRequest();
    }

    @Test
    void canInstantiate() {
        CreateUserRequest req = new CreateUserRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetUsername_ShouldWorkCorrectly() {
        // Act
        request.setUsername("johndoe");

        // Assert
        assertEquals("johndoe", request.getUsername());
    }

    @Test
    void setAndGetName_ShouldWorkCorrectly() {
        // Act
        request.setName("John Doe");

        // Assert
        assertEquals("John Doe", request.getName());
    }

    @Test
    void setAndGetEmail_ShouldWorkCorrectly() {
        // Act
        request.setEmail("john.doe@example.com");

        // Assert
        assertEquals("john.doe@example.com", request.getEmail());
    }

    @Test
    void setAndGetPassword_ShouldWorkCorrectly() {
        // Act
        request.setPassword("securePassword123");

        // Assert
        assertEquals("securePassword123", request.getPassword());
    }

    @Test
    void setAndGetBio_ShouldWorkCorrectly() {
        // Act
        request.setBio("Travel enthusiast from Amsterdam");

        // Assert
        assertEquals("Travel enthusiast from Amsterdam", request.getBio());
    }

    @Test
    void setAndGetLocation_ShouldWorkCorrectly() {
        // Act
        request.setLocation("Amsterdam, Netherlands");

        // Assert
        assertEquals("Amsterdam, Netherlands", request.getLocation());
    }

    @Test
    void setAndGetFavoriteDestination_ShouldWorkCorrectly() {
        // Act
        request.setFavoriteDestination("Barcelona, Spain");

        // Assert
        assertEquals("Barcelona, Spain", request.getFavoriteDestination());
    }

    @Test
    void setAndGetTravelStyle_ShouldWorkCorrectly() {
        // Act
        request.setTravelStyle("Adventure");

        // Assert
        assertEquals("Adventure", request.getTravelStyle());
    }

    @Test
    void setAndGetNotifications_ShouldWorkCorrectly() {
        // Act
        request.setNotifications(false);

        // Assert
        assertFalse(request.getNotifications());
    }

    @Test
    void defaultNotifications_ShouldBeTrue() {
        // Assert
        assertTrue(request.getNotifications());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        // Act
        request.setCurrency("USD");

        // Assert
        assertEquals("USD", request.getCurrency());
    }

    @Test
    void defaultCurrency_ShouldBeEUR() {
        // Assert
        assertEquals("EUR", request.getCurrency());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword123");

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankUsername_ShouldFailValidation() {
        // Arrange
        request.setUsername("");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword123");

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username is required")));
    }

    @Test
    void tooLongUsername_ShouldFailValidation() {
        // Arrange
        String longUsername = "a".repeat(31); // 31 characters
        request.setUsername(longUsername);
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword123");

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username max 30 characters")));
    }

    @Test
    void blankName_ShouldFailValidation() {
        // Arrange
        request.setUsername("johndoe");
        request.setName("");
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword123");

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void tooLongName_ShouldFailValidation() {
        // Arrange
        String longName = "a".repeat(101); // 101 characters
        request.setUsername("johndoe");
        request.setName(longName);
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword123");

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name max 100 characters")));
    }

    @Test
    void invalidEmail_ShouldFailValidation() {
        // Arrange
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("invalid-email");
        request.setPassword("securePassword123");

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must be valid")));
    }

    @Test
    void blankPassword_ShouldFailValidation() {
        // Arrange
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("");

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
    }

    @Test
    void tooLongBio_ShouldFailValidation() {
        // Arrange
        String longBio = "a".repeat(201); // 201 characters
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword123");
        request.setBio(longBio);

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Bio max 200 characters")));
    }

    @Test
    void invalidCurrency_ShouldFailValidation() {
        // Arrange
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword123");
        request.setCurrency("XY"); // Too short

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency must be valid")));
    }

    @Test
    void optionalFields_ShouldAllowNull() {
        // Arrange
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword123");
        request.setBio(null);
        request.setLocation(null);
        request.setFavoriteDestination(null);
        request.setTravelStyle(null);

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void maxLengthFields_ShouldPassValidation() {
        // Arrange
        request.setUsername("a".repeat(30)); // Max 30
        request.setName("a".repeat(100)); // Max 100
        request.setEmail("a".repeat(40) + "@test.com"); // Max 50
        request.setPassword("securePassword123");
        request.setBio("a".repeat(200)); // Max 200
        request.setLocation("a".repeat(100)); // Max 100
        request.setFavoriteDestination("a".repeat(100)); // Max 100
        request.setTravelStyle("a".repeat(50)); // Max 50
        request.setCurrency("EUR"); // Valid currency

        // Act
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }
}

