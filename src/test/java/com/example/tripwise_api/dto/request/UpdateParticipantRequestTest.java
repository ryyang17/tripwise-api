package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateParticipantRequestTest {
    private Validator validator;
    private UpdateParticipantRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new UpdateParticipantRequest("MEMBER", false);
    }

    @Test
    void canInstantiate() {
        UpdateParticipantRequest req = new UpdateParticipantRequest("role", true);
        assertNotNull(req);
        assertEquals("role", req.getRole());
        assertTrue(req.getIsOwner());
    }

    @Test
    void setAndGetRole_ShouldWorkCorrectly() {
        request.setRole("OWNER");
        assertEquals("OWNER", request.getRole());
    }

    @Test
    void setAndGetIsOwner_ShouldWorkCorrectly() {
        request.setIsOwner(true);
        assertTrue(request.getIsOwner());
        request.setIsOwner(false);
        assertFalse(request.getIsOwner());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        request.setRole("MEMBER");
        request.setIsOwner(false);
        Set<ConstraintViolation<UpdateParticipantRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankRole_ShouldFailValidation() {
        request.setRole("");
        Set<ConstraintViolation<UpdateParticipantRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Role is required")));
    }

    @Test
    void nullRole_ShouldFailValidation() {
        request.setRole(null);
        Set<ConstraintViolation<UpdateParticipantRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Role is required")));
    }

    @Test
    void isOwnerCanBeNull() {
        request.setIsOwner(null);
        assertNull(request.getIsOwner());
        Set<ConstraintViolation<UpdateParticipantRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
