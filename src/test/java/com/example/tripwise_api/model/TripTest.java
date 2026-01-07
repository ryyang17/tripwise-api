package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TripTest {

    private Trip trip;

    @BeforeEach
    void setUp() {
        trip = new Trip();
    }

    @Test
    void constructor_ShouldSetCreatedDate() {
        // Act
        Trip newTrip = new Trip();

        // Assert
        assertNotNull(newTrip.getCreatedDate());
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        // Act
        trip.setTripId(1);

        // Assert
        assertEquals(1, trip.getTripId());
    }

    @Test
    void setAndGetUserId_ShouldWorkCorrectly() {
        // Act
        trip.setUserId(1L);

        // Assert
        assertEquals(1L, trip.getUserId());
    }

    @Test
    void setAndGetTripName_ShouldWorkCorrectly() {
        // Act
        trip.setTripName("Test Trip");

        // Assert
        assertEquals("Test Trip", trip.getTripName());
    }

    @Test
    void setAndGetStartDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 3, 1);

        // Act
        trip.setStartDate(startDate);

        // Assert
        assertEquals(startDate, trip.getStartDate());
    }

    @Test
    void setAndGetEndDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate endDate = LocalDate.of(2025, 3, 5);

        // Act
        trip.setEndDate(endDate);

        // Assert
        assertEquals(endDate, trip.getEndDate());
    }

    @Test
    void setAndGetDestination_ShouldWorkCorrectly() {
        // Act
        trip.setDestination("Amsterdam");

        // Assert
        assertEquals("Amsterdam", trip.getDestination());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Act
        trip.setDescription("Test Description");

        // Assert
        assertEquals("Test Description", trip.getDescription());
    }

    @Test
    void setAndGetTransportation_ShouldWorkCorrectly() {
        // Act
        trip.setTransportation("Car");

        // Assert
        assertEquals("Car", trip.getTransportation());
    }

    @Test
    void setAndGetCreatedDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        trip.setCreatedDate(now);

        // Assert
        assertEquals(now, trip.getCreatedDate());
    }

    @Test
    void toString_ShouldContainTripInformation() {
        // Arrange
        trip.setTripId(1);
        trip.setTripName("Test Trip");
        trip.setDestination("Amsterdam");

        // Act
        String result = trip.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Trip"));
    }
}

