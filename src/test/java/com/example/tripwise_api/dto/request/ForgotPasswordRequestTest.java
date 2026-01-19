package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ForgotPasswordRequestTest {
    private Validator validator;
    private ForgotPasswordRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new ForgotPasswordRequest();
    }

    @Test
    void canInstantiate() {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        assertNotNull(req);
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        ForgotPasswordRequest req = new ForgotPasswordRequest("user@email.com");
        assertEquals("user@email.com", req.getEmail());
    }

    @Test
    void setAndGetEmail_ShouldWorkCorrectly() {
        request.setEmail("test@email.com");
        assertEquals("test@email.com", request.getEmail());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        request.setEmail("valid@email.com");
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankEmail_ShouldFailValidation() {
        request.setEmail("");
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void invalidEmail_ShouldFailValidation() {
        request.setEmail("not-an-email");
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must be valid")));
    }

    @Test
    void nullEmail_ShouldFailValidation() {
        request.setEmail(null);
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }
}
