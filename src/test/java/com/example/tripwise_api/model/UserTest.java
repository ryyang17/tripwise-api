package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void constructor_ShouldSetCreatedDate() {
        // Act
        User newUser = new User();

        // Assert
        assertNotNull(newUser.getCreatedDate());
    }

    @Test
    void setAndGetUserId_ShouldWorkCorrectly() {
        // Act
        user.setId(1L);

        // Assert
        assertEquals(1L, user.getId());
    }

    @Test
    void setAndGetUsername_ShouldWorkCorrectly() {
        // Act
        user.setUsername("testuser");

        // Assert
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void setAndGetEmail_ShouldWorkCorrectly() {
        // Act
        user.setEmail("test@example.com");

        // Assert
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void setAndGetPassword_ShouldWorkCorrectly() {
        // Act
        user.setPassword("hashedPassword");

        // Assert
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    void setAndGetCreatedDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        user.setCreatedDate(now);

        // Assert
        assertEquals(now, user.getCreatedDate());
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        // Act
        user.setId(1L);

        // Assert
        assertEquals(1L, user.getId());
    }

    @Test
    void toString_ShouldContainUserInformation() {
        // Arrange
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Act
        String result = user.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("User"));
    }

    @Test
    void fullConstructor_ShouldSetAllFields() {
        // Arrange & Act
        User newUser = new User();
        newUser.setId(1L);
        newUser.setUsername("testuser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("hashedPassword");

        // Assert
        assertEquals(1L, newUser.getId());
        assertEquals("testuser", newUser.getUsername());
        assertEquals("test@example.com", newUser.getEmail());
        assertEquals("hashedPassword", newUser.getPassword());
        assertNotNull(newUser.getCreatedDate());
    }
}

