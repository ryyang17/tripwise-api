package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {

    private Expense expense;
    private ExpenseCategory category;

    @BeforeEach
    void setUp() {
        expense = new Expense();
        category = new ExpenseCategory("Food", "Food expenses");
        category.setCategoryId(1L);
    }

    @Test
    void constructor_ShouldSetCreatedDateAndDefaults() {
        // Act
        Expense newExpense = new Expense();

        // Assert
        assertNotNull(newExpense.getCreatedDate());
        assertEquals("EUR", newExpense.getCurrency());
        assertEquals(BigDecimal.ONE, newExpense.getExchangeRate());
        assertFalse(newExpense.isValidated());
    }

    @Test
    void fullConstructor_ShouldSetAllFields() {
        // Arrange
        int tripId = 1;
        String description = "Dinner at restaurant";
        BigDecimal amount = new BigDecimal("50.00");
        LocalDate expenseDate = LocalDate.now();
        String receiptUrl = "http://example.com/receipt.jpg";

        // Act
        Expense newExpense = new Expense(tripId, description, amount, category, expenseDate, receiptUrl);

        // Assert
        assertEquals(tripId, newExpense.getTripId());
        assertEquals(description, newExpense.getDescription());
        assertEquals(amount, newExpense.getAmount());
        assertEquals(amount, newExpense.getOriginalAmount());
        assertEquals(category, newExpense.getCategory());
        assertEquals(expenseDate, newExpense.getExpenseDate());
        assertEquals(receiptUrl, newExpense.getReceiptUrl());
        assertNotNull(newExpense.getCreatedDate());
    }

    @Test
    void setAndGetExpenseId_ShouldWorkCorrectly() {
        // Act
        expense.setExpenseId(1);

        // Assert
        assertEquals(1, expense.getExpenseId());
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        // Act
        expense.setTripId(5);

        // Assert
        assertEquals(5, expense.getTripId());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Act
        expense.setDescription("Hotel booking");

        // Assert
        assertEquals("Hotel booking", expense.getDescription());
    }

    @Test
    void setAndGetAmount_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal amount = new BigDecimal("150.75");

        // Act
        expense.setAmount(amount);

        // Assert
        assertEquals(amount, expense.getAmount());
    }

    @Test
    void setAndGetOriginalAmount_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal originalAmount = new BigDecimal("100.00");

        // Act
        expense.setOriginalAmount(originalAmount);

        // Assert
        assertEquals(originalAmount, expense.getOriginalAmount());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        // Act
        expense.setCurrency("USD");

        // Assert
        assertEquals("USD", expense.getCurrency());
    }

    @Test
    void setAndGetExchangeRate_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal exchangeRate = new BigDecimal("1.18");

        // Act
        expense.setExchangeRate(exchangeRate);

        // Assert
        assertEquals(exchangeRate, expense.getExchangeRate());
    }

    @Test
    void setAndGetCategory_ShouldWorkCorrectly() {
        // Act
        expense.setCategory(category);

        // Assert
        assertEquals(category, expense.getCategory());
    }

    @Test
    void setAndGetExpenseDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 1, 15);

        // Act
        expense.setExpenseDate(date);

        // Assert
        assertEquals(date, expense.getExpenseDate());
    }

    @Test
    void setAndGetCreatedDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.now();

        // Act
        expense.setCreatedDate(dateTime);

        // Assert
        assertEquals(dateTime, expense.getCreatedDate());
    }

    @Test
    void setAndGetReceiptUrl_ShouldWorkCorrectly() {
        // Act
        expense.setReceiptUrl("https://example.com/receipt.pdf");

        // Assert
        assertEquals("https://example.com/receipt.pdf", expense.getReceiptUrl());
    }

    @Test
    void setAndGetValidated_ShouldWorkCorrectly() {
        // Act
        expense.setValidated(true);

        // Assert
        assertTrue(expense.isValidated());
    }

    @Test
    void setAndGetPaidBy_ShouldWorkCorrectly() {
        // Act
        expense.setPaidBy(10L);

        // Assert
        assertEquals(10L, expense.getPaidBy());
    }

    @Test
    void setAndGetPaidByUser_ShouldWorkCorrectly() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Act
        expense.setPaidByUser(user);

        // Assert
        assertEquals(user, expense.getPaidByUser());
    }

    @Test
    void setAndGetSplitWithUserIds_ShouldWorkCorrectly() {
        // Arrange
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);

        // Act
        expense.setSplitWithUserIds(userIds);

        // Assert
        assertEquals(userIds, expense.getSplitWithUserIds());
    }

    @Test
    void setAndGetSplitEvenly_ShouldWorkCorrectly() {
        // Act
        expense.setSplitEvenly(true);

        // Assert
        assertTrue(expense.getSplitEvenly());
    }

    @Test
    void setAndGetIsSharedExpense_ShouldWorkCorrectly() {
        // Act
        expense.setIsSharedExpense(true);

        // Assert
        assertTrue(expense.getIsSharedExpense());
    }

    @Test
    void setAndGetPaidById_ShouldWorkCorrectly() {
        // Act
        expense.setPaidById(5);

        // Assert
        assertEquals(5, expense.getPaidById());
    }

    @Test
    void setAndGetSplitWith_ShouldWorkCorrectly() {
        // Arrange
        List<Integer> participantIds = Arrays.asList(1, 2, 3);

        // Act
        expense.setSplitWith(participantIds);

        // Assert
        assertEquals(participantIds, expense.getSplitWith());
    }

    @Test
    void setAndGetCustomSplits_ShouldWorkCorrectly() {
        // Arrange
        Map<Integer, Double> splits = new HashMap<>();
        splits.put(1, 50.0);
        splits.put(2, 30.0);
        splits.put(3, 20.0);

        // Act
        expense.setCustomSplits(splits);

        // Assert
        assertEquals(splits, expense.getCustomSplits());
    }

    @Test
    void isSharedExpense_ShouldReturnFalseWhenNull() {
        // Act
        expense.setIsSharedExpense(null);

        // Assert
        assertFalse(expense.isSharedExpense());
    }

    @Test
    void isSharedExpense_ShouldReturnFalseWhenFalse() {
        // Act
        expense.setIsSharedExpense(false);

        // Assert
        assertFalse(expense.isSharedExpense());
    }

    @Test
    void isSharedExpense_ShouldReturnTrueWhenTrue() {
        // Act
        expense.setIsSharedExpense(true);

        // Assert
        assertTrue(expense.isSharedExpense());
    }

    @Test
    void splitEvenly_ShouldDefaultToFalse() {
        // Act
        Expense newExpense = new Expense();

        // Assert
        assertFalse(newExpense.getSplitEvenly());
    }

    @Test
    void isSharedExpense_ShouldDefaultToFalse() {
        // Act
        Expense newExpense = new Expense();

        // Assert
        assertFalse(newExpense.getIsSharedExpense());
    }
}

