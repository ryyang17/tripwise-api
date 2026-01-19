package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateMapRequestTest {

    private Validator validator;
    private CreateMapRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new CreateMapRequest();
    }

    @Test
    void canInstantiate() {
        CreateMapRequest req = new CreateMapRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetWaypoints_ShouldWorkCorrectly() {
        // Act
        request.setWaypoints("52.3676,4.9041;52.3702,4.8952");

        // Assert
        assertEquals("52.3676,4.9041;52.3702,4.8952", request.getWaypoints());
    }

    @Test
    void setAndGetDistance_ShouldWorkCorrectly() {
        // Act
        request.setDistance("15.2 km");

        // Assert
        assertEquals("15.2 km", request.getDistance());
    }

    @Test
    void setAndGetDuration_ShouldWorkCorrectly() {
        // Act
        request.setDuration("22 minutes");

        // Assert
        assertEquals("22 minutes", request.getDuration());
    }

    @Test
    void setAndGetLocation_ShouldWorkCorrectly() {
        // Act
        request.setLocation("Amsterdam, Netherlands");

        // Assert
        assertEquals("Amsterdam, Netherlands", request.getLocation());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setWaypoints("52.3676,4.9041;52.3702,4.8952");
        request.setDistance("5.3 km");
        request.setDuration("18 minutes");
        request.setLocation("Amsterdam");

        // Act
        Set<ConstraintViolation<CreateMapRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void emptyRequest_ShouldPassValidation() {
        // Act - all fields are optional
        Set<ConstraintViolation<CreateMapRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullFields_ShouldPassValidation() {
        // Arrange
        request.setWaypoints(null);
        request.setDistance(null);
        request.setDuration(null);
        request.setLocation(null);

        // Act
        Set<ConstraintViolation<CreateMapRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void complexWaypoints_ShouldWorkCorrectly() {
        // Arrange
        String complexWaypoints = "52.3676,4.9041;52.3702,4.8952;52.3730,4.8900;52.3750,4.8850";

        // Act
        request.setWaypoints(complexWaypoints);

        // Assert
        assertEquals(complexWaypoints, request.getWaypoints());
    }

    @Test
    void longDistance_ShouldWorkCorrectly() {
        // Act
        request.setDistance("1,234.56 km");

        // Assert
        assertEquals("1,234.56 km", request.getDistance());
    }

    @Test
    void longDuration_ShouldWorkCorrectly() {
        // Act
        request.setDuration("2 hours 45 minutes");

        // Assert
        assertEquals("2 hours 45 minutes", request.getDuration());
    }

    @Test
    void longLocation_ShouldWorkCorrectly() {
        // Act
        request.setLocation("Amsterdam Central Station, Stationsplein, Amsterdam, Netherlands");

        // Assert
        assertEquals("Amsterdam Central Station, Stationsplein, Amsterdam, Netherlands", request.getLocation());
    }
}

