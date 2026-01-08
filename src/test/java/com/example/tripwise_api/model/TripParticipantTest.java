package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TripParticipantTest {

    private TripParticipant tripParticipant;
    private User user;

    @BeforeEach
    void setUp() {
        tripParticipant = new TripParticipant();
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Test
    void constructor_ShouldInitialize() {
        // Act
        TripParticipant newParticipant = new TripParticipant();

        // Assert
        assertNotNull(newParticipant);
    }

    @Test
    void fullConstructor_ShouldSetAllFields() {
        // Arrange
        Integer tripId = 5;
        Long userId = 10L;
        String role = "organizer";

        // Act
        TripParticipant newParticipant = new TripParticipant(tripId, userId, role);

        // Assert
        assertEquals(tripId, newParticipant.getTripId());
        assertEquals(userId, newParticipant.getUserId());
        assertEquals(role, newParticipant.getRole());
        assertNotNull(newParticipant.getJoinedDate());
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        // Act
        tripParticipant.setId(1);

        // Assert
        assertEquals(1, tripParticipant.getId());
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        // Act
        tripParticipant.setTripId(10);

        // Assert
        assertEquals(10, tripParticipant.getTripId());
    }

    @Test
    void setAndGetUserId_ShouldWorkCorrectly() {
        // Act
        tripParticipant.setUserId(5L);

        // Assert
        assertEquals(5L, tripParticipant.getUserId());
    }

    @Test
    void setAndGetUser_ShouldWorkCorrectly() {
        // Act
        tripParticipant.setUser(user);

        // Assert
        assertEquals(user, tripParticipant.getUser());
    }

    @Test
    void setUser_ShouldSetUserId() {
        // Act
        tripParticipant.setUser(user);

        // Assert
        assertEquals(user.getId(), tripParticipant.getUserId());
    }

    @Test
    void setUser_ShouldHandleNullUser() {
        // Act
        tripParticipant.setUser(null);

        // Assert
        assertNull(tripParticipant.getUser());
    }

    @Test
    void setUserId_ShouldCreateUserProxy() {
        // Act
        tripParticipant.setUserId(10L);

        // Assert
        assertEquals(10L, tripParticipant.getUserId());
        assertNotNull(tripParticipant.getUser());
        assertEquals(10L, tripParticipant.getUser().getId());
    }

    @Test
    void setUserId_ShouldNotCreateProxyIfUserExists() {
        // Arrange
        tripParticipant.setUser(user);
        User originalUser = tripParticipant.getUser();

        // Act
        tripParticipant.setUserId(5L);

        // Assert
        assertEquals(originalUser, tripParticipant.getUser());
    }

    @Test
    void getUserId_ShouldReturnUserIdWhenUserIsSet() {
        // Arrange
        tripParticipant.setUser(user);

        // Act
        Long result = tripParticipant.getUserId();

        // Assert
        assertEquals(user.getId(), result);
    }

    @Test
    void setAndGetRole_ShouldWorkCorrectly() {
        // Act
        tripParticipant.setRole("participant");

        // Assert
        assertEquals("participant", tripParticipant.getRole());
    }

    @Test
    void setAndGetJoinedDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 8, 10, 0);

        // Act
        tripParticipant.setJoinedDate(dateTime);

        // Assert
        assertEquals(dateTime, tripParticipant.getJoinedDate());
    }

    @Test
    void fullConstructor_ShouldSetJoinedDate() {
        // Act
        TripParticipant newParticipant = new TripParticipant(1, 1L, "organizer");

        // Assert
        assertNotNull(newParticipant.getJoinedDate());
        assertTrue(newParticipant.getJoinedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void role_ShouldHandleDifferentRoles() {
        // Arrange
        String[] roles = {"organizer", "participant", "viewer", "admin"};

        for (String role : roles) {
            // Act
            tripParticipant.setRole(role);

            // Assert
            assertEquals(role, tripParticipant.getRole());
        }
    }

    @Test
    void role_ShouldAcceptNull() {
        // Act
        tripParticipant.setRole(null);

        // Assert
        assertNull(tripParticipant.getRole());
    }

    @Test
    void tripId_ShouldAcceptNull() {
        // Act
        tripParticipant.setTripId(null);

        // Assert
        assertNull(tripParticipant.getTripId());
    }

    @Test
    void userId_ShouldHandleNullGracefully() {
        // Act
        tripParticipant.setUserId(null);

        // Assert
        assertNull(tripParticipant.getUserId());
    }

    @Test
    void setUser_WithNullShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> tripParticipant.setUser(null));
    }

    @Test
    void fullConstructor_WithValidData_ShouldCreateCompleteObject() {
        // Arrange & Act
        TripParticipant newParticipant = new TripParticipant(100, 200L, "organizer");

        // Assert
        assertEquals(100, newParticipant.getTripId());
        assertEquals(200L, newParticipant.getUserId());
        assertEquals("organizer", newParticipant.getRole());
        assertNotNull(newParticipant.getJoinedDate());
    }
}

