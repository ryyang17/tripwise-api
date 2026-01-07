package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    private Activity activity;

    @BeforeEach
    void setUp() {
        activity = new Activity();
    }

    @Test
    void constructor_ShouldSetCreatedDate() {
        // Act
        Activity newActivity = new Activity();

        // Assert
        assertNotNull(newActivity.getCreatedDate());
    }

    @Test
    void constructorWithParameters_ShouldSetFields() {
        // Act
        Activity newActivity = new Activity(1L, "Test Activity", "Test Location", LocalTime.of(10, 0));

        // Assert
        assertEquals(1L, newActivity.getTripId());
        assertEquals("Test Activity", newActivity.getName());
        assertEquals("Test Location", newActivity.getLocation());
        assertEquals(LocalTime.of(10, 0), newActivity.getTime());
        assertNotNull(newActivity.getCreatedDate());
    }

    @Test
    void setAndGetActivityId_ShouldWorkCorrectly() {
        // Act
        activity.setActivityId(1L);

        // Assert
        assertEquals(1L, activity.getActivityId());
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        // Act
        activity.setTripId(1L);

        // Assert
        assertEquals(1L, activity.getTripId());
    }

    @Test
    void setAndGetName_ShouldWorkCorrectly() {
        // Act
        activity.setName("Test Activity");

        // Assert
        assertEquals("Test Activity", activity.getName());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Act
        activity.setDescription("Test Description");

        // Assert
        assertEquals("Test Description", activity.getDescription());
    }

    @Test
    void setAndGetDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 12, 2);

        // Act
        activity.setDate(date);

        // Assert
        assertEquals(date, activity.getDate());
    }

    @Test
    void setAndGetTime_ShouldWorkCorrectly() {
        // Arrange
        LocalTime time = LocalTime.of(10, 0);

        // Act
        activity.setTime(time);

        // Assert
        assertEquals(time, activity.getTime());
    }

    @Test
    void setAndGetLocation_ShouldWorkCorrectly() {
        // Act
        activity.setLocation("Test Location");

        // Assert
        assertEquals("Test Location", activity.getLocation());
    }

    @Test
    void setAndGetEstimatedCost_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal cost = new BigDecimal("50.00");

        // Act
        activity.setEstimatedCost(cost);

        // Assert
        assertEquals(cost, activity.getEstimatedCost());
    }

    @Test
    void setEstimatedCost_WithNull_ShouldAcceptNull() {
        // Act
        activity.setEstimatedCost(null);

        // Assert
        assertNull(activity.getEstimatedCost());
    }

    @Test
    void toString_ShouldContainActivityInformation() {
        // Arrange
        activity.setActivityId(1L);
        activity.setName("Test Activity");
        activity.setLocation("Test Location");

        // Act
        String result = activity.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Activity"));
    }
}

