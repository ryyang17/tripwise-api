package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseCategoryTest {

    private ExpenseCategory expenseCategory;

    @BeforeEach
    void setUp() {
        expenseCategory = new ExpenseCategory();
    }

    @Test
    void constructor_ShouldSetCreatedDateAndDefaults() {
        // Act
        ExpenseCategory newCategory = new ExpenseCategory();

        // Assert
        assertNotNull(newCategory.getCreatedDate());
        assertTrue(newCategory.getIsActive());
    }

    @Test
    void fullConstructor_ShouldSetAllFields() {
        // Arrange & Act
        ExpenseCategory newCategory = new ExpenseCategory("Transportation", "Transport related expenses");

        // Assert
        assertEquals("Transportation", newCategory.getCategoryName());
        assertEquals("Transport related expenses", newCategory.getCategoryDescription());
        assertNotNull(newCategory.getCreatedDate());
        assertTrue(newCategory.getIsActive());
    }

    @Test
    void setAndGetCategoryId_ShouldWorkCorrectly() {
        // Act
        expenseCategory.setCategoryId(1L);

        // Assert
        assertEquals(1L, expenseCategory.getCategoryId());
    }

    @Test
    void setAndGetCategoryName_ShouldWorkCorrectly() {
        // Act
        expenseCategory.setCategoryName("Food");

        // Assert
        assertEquals("Food", expenseCategory.getCategoryName());
    }

    @Test
    void setAndGetCategoryDescription_ShouldWorkCorrectly() {
        // Act
        expenseCategory.setCategoryDescription("All food and dining expenses");

        // Assert
        assertEquals("All food and dining expenses", expenseCategory.getCategoryDescription());
    }

    @Test
    void setAndGetIsActive_ShouldWorkCorrectly() {
        // Act
        expenseCategory.setIsActive(false);

        // Assert
        assertFalse(expenseCategory.getIsActive());
    }

    @Test
    void setAndGetCreatedDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 8, 10, 30);

        // Act
        expenseCategory.setCreatedDate(dateTime);

        // Assert
        assertEquals(dateTime, expenseCategory.getCreatedDate());
    }

    @Test
    void isActive_ShouldDefaultToTrue() {
        // Act
        ExpenseCategory newCategory = new ExpenseCategory();

        // Assert
        assertTrue(newCategory.getIsActive());
    }

    @Test
    void isActive_ShouldBeMutable() {
        // Arrange
        ExpenseCategory newCategory = new ExpenseCategory("Lodging", "Hotel and accommodation");

        // Act
        newCategory.setIsActive(false);

        // Assert
        assertFalse(newCategory.getIsActive());
    }

    @Test
    void categoryName_ShouldAcceptVariousStrings() {
        // Arrange
        String[] names = {"Food", "Transportation", "Lodging", "Entertainment", "Shopping"};

        for (String name : names) {
            // Act
            expenseCategory.setCategoryName(name);

            // Assert
            assertEquals(name, expenseCategory.getCategoryName());
        }
    }

    @Test
    void categoryDescription_ShouldAcceptLongText() {
        // Arrange
        String longDescription = "This is a very long description that contains multiple words and " +
                "provides detailed information about the expense category. It can contain up to 200 characters.";

        // Act
        expenseCategory.setCategoryDescription(longDescription);

        // Assert
        assertEquals(longDescription, expenseCategory.getCategoryDescription());
    }

    @Test
    void categoryDescription_ShouldAcceptNull() {
        // Act
        expenseCategory.setCategoryDescription(null);

        // Assert
        assertNull(expenseCategory.getCategoryDescription());
    }

    @Test
    void categoryId_ShouldAcceptLongValues() {
        // Act
        expenseCategory.setCategoryId(999999L);

        // Assert
        assertEquals(999999L, expenseCategory.getCategoryId());
    }
}

