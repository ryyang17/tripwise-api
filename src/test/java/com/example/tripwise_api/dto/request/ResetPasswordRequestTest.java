package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordRequestTest {
    private Validator validator;
    private ResetPasswordRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new ResetPasswordRequest();
    }

    @Test
    void canInstantiate() {
        ResetPasswordRequest req = new ResetPasswordRequest();
        assertNotNull(req);
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        ResetPasswordRequest req = new ResetPasswordRequest("token123", "newpass", "newpass");
        assertEquals("token123", req.getToken());
        assertEquals("newpass", req.getNewPassword());
        assertEquals("newpass", req.getConfirmPassword());
    }

    @Test
    void setAndGetToken_ShouldWorkCorrectly() {
        request.setToken("tokenABC");
        assertEquals("tokenABC", request.getToken());
    }

    @Test
    void setAndGetNewPassword_ShouldWorkCorrectly() {
        request.setNewPassword("password1");
        assertEquals("password1", request.getNewPassword());
    }

    @Test
    void setAndGetConfirmPassword_ShouldWorkCorrectly() {
        request.setConfirmPassword("password1");
        assertEquals("password1", request.getConfirmPassword());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        request.setToken("token123");
        request.setNewPassword("abcdef");
        request.setConfirmPassword("abcdef");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankToken_ShouldFailValidation() {
        request.setToken("");
        request.setNewPassword("abcdef");
        request.setConfirmPassword("abcdef");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Token is required")));
    }

    @Test
    void blankNewPassword_ShouldFailValidation() {
        request.setToken("token123");
        request.setNewPassword("");
        request.setConfirmPassword("abcdef");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("New password is required")));
    }

    @Test
    void shortNewPassword_ShouldFailValidation() {
        request.setToken("token123");
        request.setNewPassword("abc");
        request.setConfirmPassword("abc");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be at least 6 characters")));
    }

    @Test
    void blankConfirmPassword_ShouldFailValidation() {
        request.setToken("token123");
        request.setNewPassword("abcdef");
        request.setConfirmPassword("");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Confirm password is required")));
    }

    @Test
    void nullFields_ShouldFailValidation() {
        request.setToken(null);
        request.setNewPassword(null);
        request.setConfirmPassword(null);
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        assertEquals(3, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Token is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("New password is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Confirm password is required")));
    }
}
