package com.example.tripwise_api.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TripTest {
    @Test
    void testConstructorAndGetters() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 10);
        LocalDateTime created = LocalDateTime.now();
        Trip trip = new Trip(1, 2L, "TestTrip", start, end, "Paris", "Description", "Car", created);
        assertEquals(1, trip.getTripId());
        assertEquals(2L, trip.getUserId());
        assertEquals("TestTrip", trip.getTripName());
        assertEquals(start, trip.getStartDate());
        assertEquals(end, trip.getEndDate());
        assertEquals("Paris", trip.getDestination());
        assertEquals("Description", trip.getDescription());
        assertEquals("Car", trip.getTransportation());
        assertEquals(created, trip.getCreatedDate());
    }

    @Test
    void testDefaultConstructor() {
        Trip trip = new Trip();
        assertNotNull(trip.getCreatedDate());
    }
}

