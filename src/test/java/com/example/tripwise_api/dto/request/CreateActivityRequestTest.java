package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateActivityRequestTest {

    private Validator validator;
    private CreateActivityRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new CreateActivityRequest();
    }

    @Test
    void canInstantiate() {
        CreateActivityRequest req = new CreateActivityRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        // Act
        request.setTripId(1L);

        // Assert
        assertEquals(1L, request.getTripId());
    }

    @Test
    void setAndGetName_ShouldWorkCorrectly() {
        // Act
        request.setName("Test Activity");

        // Assert
        assertEquals("Test Activity", request.getName());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Act
        request.setDescription("Test Description");

        // Assert
        assertEquals("Test Description", request.getDescription());
    }

    @Test
    void setAndGetDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 3, 15);

        // Act
        request.setDate(date);

        // Assert
        assertEquals(date, request.getDate());
    }

    @Test
    void setAndGetTime_ShouldWorkCorrectly() {
        // Act
        request.setTime("14:30");

        // Assert
        assertEquals("14:30", request.getTime());
    }

    @Test
    void setAndGetLocation_ShouldWorkCorrectly() {
        // Act
        request.setLocation("Amsterdam");

        // Assert
        assertEquals("Amsterdam", request.getLocation());
    }

    @Test
    void setAndGetEstimatedCost_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal cost = new BigDecimal("99.99");

        // Act
        request.setEstimatedCost(cost);

        // Assert
        assertEquals(cost, request.getEstimatedCost());
    }

    @Test
    void setAndGetLocationDetails_ShouldWorkCorrectly() {
        // Arrange
        CreateActivityRequest.LocationDetails locationDetails = new CreateActivityRequest.LocationDetails();
        locationDetails.setAddress("Test Address");
        locationDetails.setLatitude(52.3676);
        locationDetails.setLongitude(4.9041);
        locationDetails.setPlaceId("test-place-id");

        // Act
        request.setLocationDetails(locationDetails);

        // Assert
        CreateActivityRequest.LocationDetails result = request.getLocationDetails();
        assertNotNull(result);
        assertEquals("Test Address", result.getAddress());
        assertEquals(52.3676, result.getLatitude());
        assertEquals(4.9041, result.getLongitude());
        assertEquals("test-place-id", result.getPlaceId());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setTripId(1L);
        request.setName("Test Activity");
        request.setLocation("Amsterdam");

        // Act
        Set<ConstraintViolation<CreateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullTripId_ShouldFailValidation() {
        // Arrange
        request.setTripId(null);
        request.setName("Test Activity");
        request.setLocation("Amsterdam");

        // Act
        Set<ConstraintViolation<CreateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Trip ID is required")));
    }

    @Test
    void blankName_ShouldFailValidation() {
        // Arrange
        request.setTripId(1L);
        request.setName("");
        request.setLocation("Amsterdam");

        // Act
        Set<ConstraintViolation<CreateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Activity name is required")));
    }

    @Test
    void blankLocation_ShouldFailValidation() {
        // Arrange
        request.setTripId(1L);
        request.setName("Test Activity");
        request.setLocation("");

        // Act
        Set<ConstraintViolation<CreateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Location is required")));
    }
}
