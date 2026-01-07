package com.example.tripwise_api.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TripResponseTest {

    private TripResponse tripResponse;

    @BeforeEach
    void setUp() {
        tripResponse = new TripResponse();
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        // Act
        tripResponse.setTripId(1);

        // Assert
        assertEquals(1, tripResponse.getTripId());
    }

    @Test
    void setAndGetUserId_ShouldWorkCorrectly() {
        // Act
        tripResponse.setUserId(1L);

        // Assert
        assertEquals(1L, tripResponse.getUserId());
    }

    @Test
    void setAndGetTripName_ShouldWorkCorrectly() {
        // Act
        tripResponse.setTripName("Test Trip");

        // Assert
        assertEquals("Test Trip", tripResponse.getTripName());
    }

    @Test
    void setAndGetStartDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 3, 1);

        // Act
        tripResponse.setStartDate(startDate);

        // Assert
        assertEquals(startDate, tripResponse.getStartDate());
    }

    @Test
    void setAndGetEndDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate endDate = LocalDate.of(2025, 3, 5);

        // Act
        tripResponse.setEndDate(endDate);

        // Assert
        assertEquals(endDate, tripResponse.getEndDate());
    }

    @Test
    void setAndGetDestination_ShouldWorkCorrectly() {
        // Act
        tripResponse.setDestination("Amsterdam");

        // Assert
        assertEquals("Amsterdam", tripResponse.getDestination());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Act
        tripResponse.setDescription("Test Description");

        // Assert
        assertEquals("Test Description", tripResponse.getDescription());
    }

    @Test
    void setAndGetTransportation_ShouldWorkCorrectly() {
        // Act
        tripResponse.setTransportation("Car");

        // Assert
        assertEquals("Car", tripResponse.getTransportation());
    }

    @Test
    void setAndGetCreatedDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        tripResponse.setCreatedDate(now);

        // Assert
        assertEquals(now, tripResponse.getCreatedDate());
    }

    @Test
    void allFieldsCanBeSet_ShouldWorkCorrectly() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 5);
        LocalDateTime createdDate = LocalDateTime.now();

        // Act
        tripResponse.setTripId(1);
        tripResponse.setUserId(1L);
        tripResponse.setTripName("Test Trip");
        tripResponse.setStartDate(startDate);
        tripResponse.setEndDate(endDate);
        tripResponse.setDestination("Amsterdam");
        tripResponse.setDescription("Test Description");
        tripResponse.setTransportation("Car");
        tripResponse.setCreatedDate(createdDate);

        // Assert
        assertEquals(1, tripResponse.getTripId());
        assertEquals(1L, tripResponse.getUserId());
        assertEquals("Test Trip", tripResponse.getTripName());
        assertEquals(startDate, tripResponse.getStartDate());
        assertEquals(endDate, tripResponse.getEndDate());
        assertEquals("Amsterdam", tripResponse.getDestination());
        assertEquals("Test Description", tripResponse.getDescription());
        assertEquals("Car", tripResponse.getTransportation());
        assertEquals(createdDate, tripResponse.getCreatedDate());
    }
}

