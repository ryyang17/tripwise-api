package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestTest {
    private Validator validator;
    private UpdateUserRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new UpdateUserRequest();
    }

    @Test
    void canInstantiate() {
        UpdateUserRequest req = new UpdateUserRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetUsername_ShouldWorkCorrectly() {
        request.setUsername("johndoe");
        assertEquals("johndoe", request.getUsername());
    }

    @Test
    void setAndGetName_ShouldWorkCorrectly() {
        request.setName("John Doe");
        assertEquals("John Doe", request.getName());
    }

    @Test
    void setAndGetEmail_ShouldWorkCorrectly() {
        request.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", request.getEmail());
    }

    @Test
    void setAndGetBio_ShouldWorkCorrectly() {
        request.setBio("Travel lover");
        assertEquals("Travel lover", request.getBio());
    }

    @Test
    void setAndGetLocation_ShouldWorkCorrectly() {
        request.setLocation("Amsterdam");
        assertEquals("Amsterdam", request.getLocation());
    }

    @Test
    void setAndGetFavoriteDestination_ShouldWorkCorrectly() {
        request.setFavoriteDestination("Barcelona");
        assertEquals("Barcelona", request.getFavoriteDestination());
    }

    @Test
    void setAndGetTravelStyle_ShouldWorkCorrectly() {
        request.setTravelStyle("Adventure");
        assertEquals("Adventure", request.getTravelStyle());
    }

    @Test
    void setAndGetNotifications_ShouldWorkCorrectly() {
        request.setNotifications(true);
        assertTrue(request.getNotifications());
        request.setNotifications(false);
        assertFalse(request.getNotifications());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        request.setCurrency("USD");
        assertEquals("USD", request.getCurrency());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setBio("Travel lover");
        request.setLocation("Amsterdam");
        request.setFavoriteDestination("Barcelona");
        request.setTravelStyle("Adventure");
        request.setNotifications(true);
        request.setCurrency("EUR");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankUsername_ShouldFailValidation() {
        request.setUsername("");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username is required")));
    }

    @Test
    void tooLongUsername_ShouldFailValidation() {
        request.setUsername("a".repeat(31));
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username max 30 characters")));
    }

    @Test
    void blankName_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("");
        request.setEmail("john.doe@example.com");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void tooLongName_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("a".repeat(101));
        request.setEmail("john.doe@example.com");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name max 100 characters")));
    }

    @Test
    void blankEmail_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void invalidEmail_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("not-an-email");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must be valid")));
    }

    @Test
    void tooLongEmail_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        // 42 'a's + 9 = 51
        request.setEmail("a".repeat(42) + "@test.com");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email max 50 characters")));
    }

    @Test
    void tooLongBio_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setBio("a".repeat(201));
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Bio max 200 characters")));
    }

    @Test
    void tooLongLocation_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setLocation("a".repeat(101));
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Location max 100 characters")));
    }

    @Test
    void tooLongFavoriteDestination_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setFavoriteDestination("a".repeat(101));
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Favorite destination max 100 characters")));
    }

    @Test
    void tooLongTravelStyle_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setTravelStyle("a".repeat(51));
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Travel style max 50 characters")));
    }

    @Test
    void invalidCurrency_ShouldFailValidation() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setCurrency("XY");
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency must be valid")));
    }

    @Test
    void optionalFields_ShouldAllowNull() {
        request.setUsername("johndoe");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setBio(null);
        request.setLocation(null);
        request.setFavoriteDestination(null);
        request.setTravelStyle(null);
        request.setNotifications(null);
        request.setCurrency(null);
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
