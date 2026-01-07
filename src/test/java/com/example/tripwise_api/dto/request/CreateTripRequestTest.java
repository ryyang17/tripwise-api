package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateTripRequestTest {

    private Validator validator;
    private CreateTripRequest createTripRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        createTripRequest = new CreateTripRequest();
    }

    @Test
    void validCreateTripRequest_ShouldPassValidation() {
        // Arrange
        createTripRequest.setUserId(1L);
        createTripRequest.setTripName("Test Trip");
        createTripRequest.setStartDate(LocalDate.of(2025, 3, 1));
        createTripRequest.setEndDate(LocalDate.of(2025, 3, 5));
        createTripRequest.setDestination("Amsterdam");

        // Act
        Set<ConstraintViolation<CreateTripRequest>> violations = validator.validate(createTripRequest);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void setAndGetUserId_ShouldWorkCorrectly() {
        // Act
        createTripRequest.setUserId(1L);

        // Assert
        assertEquals(1L, createTripRequest.getUserId());
    }

    @Test
    void setAndGetTripName_ShouldWorkCorrectly() {
        // Act
        createTripRequest.setTripName("Test Trip");

        // Assert
        assertEquals("Test Trip", createTripRequest.getTripName());
    }

    @Test
    void setAndGetStartDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 3, 1);

        // Act
        createTripRequest.setStartDate(startDate);

        // Assert
        assertEquals(startDate, createTripRequest.getStartDate());
    }

    @Test
    void setAndGetEndDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate endDate = LocalDate.of(2025, 3, 5);

        // Act
        createTripRequest.setEndDate(endDate);

        // Assert
        assertEquals(endDate, createTripRequest.getEndDate());
    }

    @Test
    void setAndGetDestination_ShouldWorkCorrectly() {
        // Act
        createTripRequest.setDestination("Amsterdam");

        // Assert
        assertEquals("Amsterdam", createTripRequest.getDestination());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Act
        createTripRequest.setDescription("Test Description");

        // Assert
        assertEquals("Test Description", createTripRequest.getDescription());
    }

    @Test
    void setAndGetTransportation_ShouldWorkCorrectly() {
        // Act
        createTripRequest.setTransportation("Car");

        // Assert
        assertEquals("Car", createTripRequest.getTransportation());
    }

    @Test
    void nullUserId_ShouldFailValidation() {
        // Arrange
        createTripRequest.setUserId(null);
        createTripRequest.setTripName("Test Trip");
        createTripRequest.setStartDate(LocalDate.of(2025, 3, 1));
        createTripRequest.setEndDate(LocalDate.of(2025, 3, 5));
        createTripRequest.setDestination("Amsterdam");

        // Act
        Set<ConstraintViolation<CreateTripRequest>> violations = validator.validate(createTripRequest);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void blankTripName_ShouldFailValidation() {
        // Arrange
        createTripRequest.setUserId(1L);
        createTripRequest.setTripName("");
        createTripRequest.setStartDate(LocalDate.of(2025, 3, 1));
        createTripRequest.setEndDate(LocalDate.of(2025, 3, 5));
        createTripRequest.setDestination("Amsterdam");

        // Act
        Set<ConstraintViolation<CreateTripRequest>> violations = validator.validate(createTripRequest);

        // Assert
        assertFalse(violations.isEmpty());
    }
}

