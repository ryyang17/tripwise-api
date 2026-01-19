package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateParticipantRequestTest {

    private Validator validator;
    private CreateParticipantRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new CreateParticipantRequest();
    }

    @Test
    void canInstantiate() {
        CreateParticipantRequest req = new CreateParticipantRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetUserId_ShouldWorkCorrectly() {
        // Act
        request.setUserId(123L);

        // Assert
        assertEquals(123L, request.getUserId());
    }

    @Test
    void setAndGetUserName_ShouldWorkCorrectly() {
        // Act
        request.setUserName("John Doe");

        // Assert
        assertEquals("John Doe", request.getUserName());
    }

    @Test
    void setAndGetUserEmail_ShouldWorkCorrectly() {
        // Act
        request.setUserEmail("john.doe@example.com");

        // Assert
        assertEquals("john.doe@example.com", request.getUserEmail());
    }

    @Test
    void setAndGetRole_ShouldWorkCorrectly() {
        // Act
        request.setRole("MEMBER");

        // Assert
        assertEquals("MEMBER", request.getRole());
    }

    @Test
    void setAndGetIsOwner_ShouldWorkCorrectly() {
        // Act
        request.setIsOwner(true);

        // Assert
        assertTrue(request.getIsOwner());
    }

    @Test
    void defaultIsOwner_ShouldBeFalse() {
        // Assert
        assertFalse(request.getIsOwner());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setUserId(123L);
        request.setUserName("John Doe");
        request.setUserEmail("john.doe@example.com");
        request.setRole("MEMBER");

        // Act
        Set<ConstraintViolation<CreateParticipantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullUserId_ShouldFailValidation() {
        // Arrange
        request.setUserId(null);
        request.setUserName("John Doe");
        request.setUserEmail("john.doe@example.com");

        // Act
        Set<ConstraintViolation<CreateParticipantRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("User ID is required")));
    }

    @Test
    void optionalFields_ShouldAllowNull() {
        // Arrange
        request.setUserId(123L);
        request.setUserName(null);
        request.setUserEmail(null);
        request.setRole(null);

        // Act
        Set<ConstraintViolation<CreateParticipantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void ownerRole_ShouldWorkCorrectly() {
        // Arrange
        request.setUserId(123L);
        request.setUserName("Trip Owner");
        request.setRole("OWNER");
        request.setIsOwner(true);

        // Act
        Set<ConstraintViolation<CreateParticipantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
        assertTrue(request.getIsOwner());
        assertEquals("OWNER", request.getRole());
    }

    @Test
    void memberRole_ShouldWorkCorrectly() {
        // Arrange
        request.setUserId(456L);
        request.setUserName("Regular Member");
        request.setRole("MEMBER");
        request.setIsOwner(false);

        // Act
        Set<ConstraintViolation<CreateParticipantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
        assertFalse(request.getIsOwner());
        assertEquals("MEMBER", request.getRole());
    }

    @Test
    void emptyStrings_ShouldBeAllowed() {
        // Arrange
        request.setUserId(123L);
        request.setUserName("");
        request.setUserEmail("");
        request.setRole("");

        // Act
        Set<ConstraintViolation<CreateParticipantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }
}
