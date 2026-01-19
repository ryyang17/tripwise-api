package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordRequestTest {
    private Validator validator;
    private ChangePasswordRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new ChangePasswordRequest();
    }

    @Test
    void canInstantiate() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        assertNotNull(req);
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        ChangePasswordRequest req = new ChangePasswordRequest("oldpass", "newpass1", "newpass1");
        assertEquals("oldpass", req.getCurrentPassword());
        assertEquals("newpass1", req.getNewPassword());
        assertEquals("newpass1", req.getConfirmPassword());
    }

    @Test
    void setAndGetCurrentPassword_ShouldWorkCorrectly() {
        request.setCurrentPassword("oldpass");
        assertEquals("oldpass", request.getCurrentPassword());
    }

    @Test
    void setAndGetNewPassword_ShouldWorkCorrectly() {
        request.setNewPassword("newpass2");
        assertEquals("newpass2", request.getNewPassword());
    }

    @Test
    void setAndGetConfirmPassword_ShouldWorkCorrectly() {
        request.setConfirmPassword("newpass2");
        assertEquals("newpass2", request.getConfirmPassword());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        request.setCurrentPassword("oldpass");
        request.setNewPassword("abcdef");
        request.setConfirmPassword("abcdef");
        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankCurrentPassword_ShouldFailValidation() {
        request.setCurrentPassword("");
        request.setNewPassword("abcdef");
        request.setConfirmPassword("abcdef");
        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Current password is required")));
    }

    @Test
    void blankNewPassword_ShouldFailValidation() {
        request.setCurrentPassword("oldpass");
        request.setNewPassword("");
        request.setConfirmPassword("abcdef");
        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("New password is required")));
    }

    @Test
    void shortNewPassword_ShouldFailValidation() {
        request.setCurrentPassword("oldpass");
        request.setNewPassword("abc");
        request.setConfirmPassword("abc");
        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("New password must be at least 6 characters")));
    }

    @Test
    void blankConfirmPassword_ShouldFailValidation() {
        request.setCurrentPassword("oldpass");
        request.setNewPassword("abcdef");
        request.setConfirmPassword("");
        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Confirm password is required")));
    }

    @Test
    void nullFields_ShouldFailValidation() {
        request.setCurrentPassword(null);
        request.setNewPassword(null);
        request.setConfirmPassword(null);
        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(3, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Current password is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("New password is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Confirm password is required")));
    }
}
