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

class UpdateActivityRequestTest {

    private Validator validator;
    private UpdateActivityRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new UpdateActivityRequest();
    }

    @Test
    void canInstantiate() {
        UpdateActivityRequest req = new UpdateActivityRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetActivityId_ShouldWorkCorrectly() {
        // Act
        request.setActivityId(5L);

        // Assert
        assertEquals(5L, request.getActivityId());
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
        request.setName("Updated Activity Name");

        // Assert
        assertEquals("Updated Activity Name", request.getName());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Act
        request.setDescription("Updated description");

        // Assert
        assertEquals("Updated description", request.getDescription());
    }

    @Test
    void setAndGetDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 2, 20);

        // Act
        request.setDate(date);

        // Assert
        assertEquals(date, request.getDate());
    }

    @Test
    void setAndGetTime_ShouldWorkCorrectly() {
        // Act
        request.setTime("16:30");

        // Assert
        assertEquals("16:30", request.getTime());
    }

    @Test
    void setAndGetLocation_ShouldWorkCorrectly() {
        // Act
        request.setLocation("Rotterdam");

        // Assert
        assertEquals("Rotterdam", request.getLocation());
    }

    @Test
    void setAndGetEstimatedCost_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal cost = new BigDecimal("75.00");

        // Act
        request.setEstimatedCost(cost);

        // Assert
        assertEquals(cost, request.getEstimatedCost());
    }

    @Test
    void setAndGetLocationDetails_ShouldWorkCorrectly() {
        // Arrange
        CreateActivityRequest.LocationDetails locationDetails = new CreateActivityRequest.LocationDetails();
        locationDetails.setAddress("Updated Address");
        locationDetails.setLatitude(51.9225);
        locationDetails.setLongitude(4.4792);
        locationDetails.setPlaceId("updated-place-id");

        // Act
        request.setLocationDetails(locationDetails);

        // Assert
        CreateActivityRequest.LocationDetails result = request.getLocationDetails();
        assertNotNull(result);
        assertEquals("Updated Address", result.getAddress());
        assertEquals(51.9225, result.getLatitude());
        assertEquals(4.4792, result.getLongitude());
        assertEquals("updated-place-id", result.getPlaceId());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setActivityId(1L);
        request.setTripId(1L);
        request.setName("Museum Visit");
        request.setLocation("Rotterdam");

        // Act
        Set<ConstraintViolation<UpdateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullActivityId_ShouldFailValidation() {
        // Arrange
        request.setActivityId(null);
        request.setTripId(1L);
        request.setName("Museum Visit");
        request.setLocation("Rotterdam");

        // Act
        Set<ConstraintViolation<UpdateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Activity ID is required")));
    }

    @Test
    void nullTripId_ShouldFailValidation() {
        // Arrange
        request.setActivityId(1L);
        request.setTripId(null);
        request.setName("Museum Visit");
        request.setLocation("Rotterdam");

        // Act
        Set<ConstraintViolation<UpdateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Trip ID is required")));
    }

    @Test
    void blankName_ShouldFailValidation() {
        // Arrange
        request.setActivityId(1L);
        request.setTripId(1L);
        request.setName("");
        request.setLocation("Rotterdam");

        // Act
        Set<ConstraintViolation<UpdateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Activity name is required")));
    }

    @Test
    void blankLocation_ShouldFailValidation() {
        // Arrange
        request.setActivityId(1L);
        request.setTripId(1L);
        request.setName("Museum Visit");
        request.setLocation("");

        // Act
        Set<ConstraintViolation<UpdateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Location is required")));
    }

    @Test
    void optionalFields_ShouldAllowNull() {
        // Arrange
        request.setActivityId(1L);
        request.setTripId(1L);
        request.setName("Museum Visit");
        request.setLocation("Rotterdam");
        request.setDescription(null);
        request.setDate(null);
        request.setTime(null);
        request.setEstimatedCost(null);
        request.setLocationDetails(null);

        // Act
        Set<ConstraintViolation<UpdateActivityRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }
}

