package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseShareTest {

    private ExpenseShare expenseShare;
    private User user;

    @BeforeEach
    void setUp() {
        expenseShare = new ExpenseShare();
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Test
    void constructor_ShouldSetDefaults() {
        // Act
        ExpenseShare newShare = new ExpenseShare();

        // Assert
        assertNotNull(newShare);
    }

    @Test
    void fullConstructor_ShouldSetAllFields() {
        // Arrange
        Integer expenseId = 1;
        Long userId = 5L;
        BigDecimal amount = new BigDecimal("50.00");

        // Act
        ExpenseShare newShare = new ExpenseShare(expenseId, userId, amount);

        // Assert
        assertEquals(expenseId, newShare.getExpenseId());
        assertEquals(userId, newShare.getUserId());
        assertEquals(amount, newShare.getAmount());
        assertFalse(newShare.getIsSettled());
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        // Act
        expenseShare.setId(1);

        // Assert
        assertEquals(1, expenseShare.getId());
    }

    @Test
    void setAndGetExpenseId_ShouldWorkCorrectly() {
        // Act
        expenseShare.setExpenseId(10);

        // Assert
        assertEquals(10, expenseShare.getExpenseId());
    }

    @Test
    void setAndGetUserId_ShouldWorkCorrectly() {
        // Act
        expenseShare.setUserId(5L);

        // Assert
        assertEquals(5L, expenseShare.getUserId());
    }

    @Test
    void setAndGetUser_ShouldWorkCorrectly() {
        // Act
        expenseShare.setUser(user);

        // Assert
        assertEquals(user, expenseShare.getUser());
    }

    @Test
    void setUser_ShouldSetUserId() {
        // Act
        expenseShare.setUser(user);

        // Assert
        assertEquals(user.getId(), expenseShare.getUserId());
    }

    @Test
    void setUser_ShouldHandleNullUser() {
        // Act
        expenseShare.setUser(null);

        // Assert
        assertNull(expenseShare.getUser());
    }

    @Test
    void setAndGetAmount_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal amount = new BigDecimal("75.50");

        // Act
        expenseShare.setAmount(amount);

        // Assert
        assertEquals(amount, expenseShare.getAmount());
    }

    @Test
    void setAndGetIsSettled_ShouldWorkCorrectly() {
        // Act
        expenseShare.setIsSettled(true);

        // Assert
        assertTrue(expenseShare.getIsSettled());
    }

    @Test
    void isSettled_ShouldDefaultToFalse() {
        // Arrange & Act
        ExpenseShare newShare = new ExpenseShare(1, 1L, new BigDecimal("100.00"));

        // Assert
        assertFalse(newShare.getIsSettled());
    }

    @Test
    void amount_ShouldHandleDecimalPrecision() {
        // Arrange
        BigDecimal preciseAmount = new BigDecimal("123.4567");

        // Act
        expenseShare.setAmount(preciseAmount);

        // Assert
        assertEquals(preciseAmount, expenseShare.getAmount());
    }

    @Test
    void amount_ShouldHandleZero() {
        // Arrange
        BigDecimal zero = BigDecimal.ZERO;

        // Act
        expenseShare.setAmount(zero);

        // Assert
        assertEquals(zero, expenseShare.getAmount());
    }

    @Test
    void amount_ShouldHandleLargeValues() {
        // Arrange
        BigDecimal largeAmount = new BigDecimal("999999.9999");

        // Act
        expenseShare.setAmount(largeAmount);

        // Assert
        assertEquals(largeAmount, expenseShare.getAmount());
    }

    @Test
    void fullConstructor_WithUserIdShouldNotSetUser() {
        // Arrange & Act
        ExpenseShare newShare = new ExpenseShare(1, 5L, new BigDecimal("50.00"));

        // Assert
        assertEquals(5L, newShare.getUserId());
        assertNull(newShare.getUser());
    }

    @Test
    void setUser_WithNullShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> expenseShare.setUser(null));
    }

    @Test
    void isSettled_CanBeSetToTrue() {
        // Act
        expenseShare.setIsSettled(true);

        // Assert
        assertTrue(expenseShare.getIsSettled());
    }

    @Test
    void isSettled_CanBeSetToFalse() {
        // Act
        expenseShare.setIsSettled(false);

        // Assert
        assertFalse(expenseShare.getIsSettled());
    }
}

