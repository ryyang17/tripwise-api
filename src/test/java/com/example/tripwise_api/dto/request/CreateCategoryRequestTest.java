package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateCategoryRequestTest {

    private Validator validator;
    private CreateCategoryRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new CreateCategoryRequest();
    }

    @Test
    void canInstantiate() {
        CreateCategoryRequest req = new CreateCategoryRequest();
        assertNotNull(req);
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        // Act
        CreateCategoryRequest req = new CreateCategoryRequest("Food", "Meals and dining expenses");

        // Assert
        assertEquals("Food", req.getCategoryName());
        assertEquals("Meals and dining expenses", req.getCategoryDescription());
        assertTrue(req.getIsActive()); // Default value
    }

    @Test
    void setAndGetCategoryName_ShouldWorkCorrectly() {
        // Act
        request.setCategoryName("Transportation");

        // Assert
        assertEquals("Transportation", request.getCategoryName());
    }

    @Test
    void setAndGetCategoryDescription_ShouldWorkCorrectly() {
        // Act
        request.setCategoryDescription("Travel and transport costs");

        // Assert
        assertEquals("Travel and transport costs", request.getCategoryDescription());
    }

    @Test
    void setAndGetIsActive_ShouldWorkCorrectly() {
        // Act
        request.setIsActive(false);

        // Assert
        assertFalse(request.getIsActive());
    }

    @Test
    void defaultIsActive_ShouldBeTrue() {
        // Assert
        assertTrue(request.getIsActive());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setCategoryName("Accommodation");
        request.setCategoryDescription("Hotel and lodging expenses");

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankCategoryName_ShouldFailValidation() {
        // Arrange
        request.setCategoryName("");
        request.setCategoryDescription("Valid description");

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category name is required")));
    }

    @Test
    void nullCategoryName_ShouldFailValidation() {
        // Arrange
        request.setCategoryName(null);
        request.setCategoryDescription("Valid description");

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category name is required")));
    }

    @Test
    void tooLongCategoryName_ShouldFailValidation() {
        // Arrange
        String longName = "a".repeat(51); // 51 characters
        request.setCategoryName(longName);
        request.setCategoryDescription("Valid description");

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category name max 50 characters")));
    }

    @Test
    void maxLengthCategoryName_ShouldPassValidation() {
        // Arrange
        String maxName = "a".repeat(50); // Exactly 50 characters
        request.setCategoryName(maxName);
        request.setCategoryDescription("Valid description");

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void tooLongCategoryDescription_ShouldFailValidation() {
        // Arrange
        String longDescription = "a".repeat(201); // 201 characters
        request.setCategoryName("Valid Name");
        request.setCategoryDescription(longDescription);

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category description max 200 characters")));
    }

    @Test
    void maxLengthCategoryDescription_ShouldPassValidation() {
        // Arrange
        String maxDescription = "a".repeat(200); // Exactly 200 characters
        request.setCategoryName("Valid Name");
        request.setCategoryDescription(maxDescription);

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullCategoryDescription_ShouldPassValidation() {
        // Arrange
        request.setCategoryName("Valid Name");
        request.setCategoryDescription(null);

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }
}
