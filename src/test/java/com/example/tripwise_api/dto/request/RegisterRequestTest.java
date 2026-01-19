package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {
    private Validator validator;
    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new RegisterRequest();
    }

    @Test
    void canInstantiate() {
        RegisterRequest req = new RegisterRequest();
        assertNotNull(req);
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        RegisterRequest req = new RegisterRequest("user1", "user1@email.com", "password123");
        assertEquals("user1", req.getUsername());
        assertEquals("user1@email.com", req.getEmail());
        assertEquals("password123", req.getPassword());
    }

    @Test
    void setAndGetUsername_ShouldWorkCorrectly() {
        request.setUsername("user2");
        assertEquals("user2", request.getUsername());
    }

    @Test
    void setAndGetEmail_ShouldWorkCorrectly() {
        request.setEmail("user2@email.com");
        assertEquals("user2@email.com", request.getEmail());
    }

    @Test
    void setAndGetPassword_ShouldWorkCorrectly() {
        request.setPassword("pass456");
        assertEquals("pass456", request.getPassword());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        request.setUsername("user3");
        request.setEmail("user3@email.com");
        request.setPassword("password789");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankUsername_ShouldFailValidation() {
        request.setUsername("");
        request.setEmail("user@email.com");
        request.setPassword("password");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username is required")));
    }

    @Test
    void shortUsername_ShouldFailValidation() {
        request.setUsername("ab");
        request.setEmail("user@email.com");
        request.setPassword("password");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username must be between 3 and 30 characters")));
    }

    @Test
    void longUsername_ShouldFailValidation() {
        request.setUsername("a".repeat(31));
        request.setEmail("user@email.com");
        request.setPassword("password");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username must be between 3 and 30 characters")));
    }

    @Test
    void blankEmail_ShouldFailValidation() {
        request.setUsername("user4");
        request.setEmail("");
        request.setPassword("password");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void invalidEmail_ShouldFailValidation() {
        request.setUsername("user5");
        request.setEmail("not-an-email");
        request.setPassword("password");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    void blankPassword_ShouldFailValidation() {
        request.setUsername("user6");
        request.setEmail("user6@email.com");
        request.setPassword("");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
    }

    @Test
    void shortPassword_ShouldFailValidation() {
        request.setUsername("user7");
        request.setEmail("user7@email.com");
        request.setPassword("123");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be at least 6 characters")));
    }

    @Test
    void nullFields_ShouldFailValidation() {
        request.setUsername(null);
        request.setEmail(null);
        request.setPassword(null);
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertEquals(3, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
    }
}
