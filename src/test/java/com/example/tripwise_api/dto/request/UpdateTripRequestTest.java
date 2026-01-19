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

class UpdateTripRequestTest {
    private Validator validator;
    private UpdateTripRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new UpdateTripRequest();
    }

    @Test
    void canInstantiate() {
        UpdateTripRequest req = new UpdateTripRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        request.setTripId(123);
        assertEquals(123, request.getTripId());
    }

    @Test
    void setAndGetUserId_ShouldWorkCorrectly() {
        request.setUserId(456L);
        assertEquals(456L, request.getUserId());
    }

    @Test
    void setAndGetTripName_ShouldWorkCorrectly() {
        request.setTripName("Summer Vacation");
        assertEquals("Summer Vacation", request.getTripName());
    }

    @Test
    void setAndGetStartDate_ShouldWorkCorrectly() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        request.setStartDate(date);
        assertEquals(date, request.getStartDate());
    }

    @Test
    void setAndGetEndDate_ShouldWorkCorrectly() {
        LocalDate date = LocalDate.of(2026, 6, 10);
        request.setEndDate(date);
        assertEquals(date, request.getEndDate());
    }

    @Test
    void setAndGetDestination_ShouldWorkCorrectly() {
        request.setDestination("Paris");
        assertEquals("Paris", request.getDestination());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        request.setDescription("A fun trip to Paris");
        assertEquals("A fun trip to Paris", request.getDescription());
    }

    @Test
    void setAndGetTransportation_ShouldWorkCorrectly() {
        request.setTransportation("plane");
        assertEquals("plane", request.getTransportation());
    }

    @Test
    void setAndGetDestinationCoords_ShouldWorkCorrectly() {
        CreateTripRequest.DestinationCoords coords = new CreateTripRequest.DestinationCoords();
        coords.setLat(48.8566);
        coords.setLng(2.3522);
        request.setDestinationCoords(coords);
        assertEquals(coords, request.getDestinationCoords());
        assertEquals(48.8566, coords.getLat());
        assertEquals(2.3522, coords.getLng());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        request.setTripId(1);
        request.setUserId(2L);
        request.setTripName("Trip");
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 10));
        request.setDestination("Rome");
        request.setTransportation("car");
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullTripId_ShouldFailValidation() {
        request.setTripId(null);
        request.setUserId(2L);
        request.setTripName("Trip");
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 10));
        request.setDestination("Rome");
        request.setTransportation("car");
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Trip ID is required")));
    }

    @Test
    void nullUserId_ShouldFailValidation() {
        request.setTripId(1);
        request.setUserId(null);
        request.setTripName("Trip");
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 10));
        request.setDestination("Rome");
        request.setTransportation("car");
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("User ID is required")));
    }

    @Test
    void blankTripName_ShouldFailValidation() {
        request.setTripId(1);
        request.setUserId(2L);
        request.setTripName("");
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 10));
        request.setDestination("Rome");
        request.setTransportation("car");
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Trip name is required")));
    }

    @Test
    void nullStartDate_ShouldFailValidation() {
        request.setTripId(1);
        request.setUserId(2L);
        request.setTripName("Trip");
        request.setStartDate(null);
        request.setEndDate(LocalDate.of(2026, 1, 10));
        request.setDestination("Rome");
        request.setTransportation("car");
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Start date is required")));
    }

    @Test
    void nullEndDate_ShouldFailValidation() {
        request.setTripId(1);
        request.setUserId(2L);
        request.setTripName("Trip");
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(null);
        request.setDestination("Rome");
        request.setTransportation("car");
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("End date is required")));
    }

    @Test
    void blankDestination_ShouldFailValidation() {
        request.setTripId(1);
        request.setUserId(2L);
        request.setTripName("Trip");
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 10));
        request.setDestination("");
        request.setTransportation("car");
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Destination is required")));
    }

    @Test
    void blankTransportation_ShouldFailValidation() {
        request.setTripId(1);
        request.setUserId(2L);
        request.setTripName("Trip");
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 10));
        request.setDestination("Rome");
        request.setTransportation("");
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Transportation is required")));
    }

    @Test
    void invalidTransportation_ShouldFailValidation() {
        request.setTripId(1);
        request.setUserId(2L);
        request.setTripName("Trip");
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 10));
        request.setDestination("Rome");
        request.setTransportation("bicycle");
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Transportation must be one of: plane, car, bus, train")));
    }

    @Test
    void validTransportationValues_ShouldPassValidation() {
        String[] valid = {"plane", "car", "bus", "train"};
        for (String t : valid) {
            request.setTripId(1);
            request.setUserId(2L);
            request.setTripName("Trip");
            request.setStartDate(LocalDate.of(2026, 1, 1));
            request.setEndDate(LocalDate.of(2026, 1, 10));
            request.setDestination("Rome");
            request.setTransportation(t);
            Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty(), "Should be valid for: " + t);
        }
    }

    @Test
    void descriptionAndDestinationCoords_ShouldBeOptional() {
        request.setTripId(1);
        request.setUserId(2L);
        request.setTripName("Trip");
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 10));
        request.setDestination("Rome");
        request.setTransportation("car");
        request.setDescription(null);
        request.setDestinationCoords(null);
        Set<ConstraintViolation<UpdateTripRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
