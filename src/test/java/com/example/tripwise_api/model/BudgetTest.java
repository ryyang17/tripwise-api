package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BudgetTest {

    private Budget budget;
    private Trip trip;

    @BeforeEach
    void setUp() {
        budget = new Budget();
        trip = new Trip();
        trip.setTripId(1);
    }

    @Test
    void constructor_ShouldSetCreatedDate() {
        // Act
        Budget newBudget = new Budget();

        // Assert
        assertNotNull(newBudget.getCreatedDate());
    }

    @Test
    void fullConstructor_ShouldSetAllFields() {
        // Arrange & Act
        Budget newBudget = new Budget(trip, 5000.0, "USD");

        // Assert
        assertEquals(trip, newBudget.getTrip());
        assertEquals(5000.0, newBudget.getTotalBudget());
        assertEquals("USD", newBudget.getCurrency());
        assertNotNull(newBudget.getCreatedDate());
    }

    @Test
    void setAndGetBudgetId_ShouldWorkCorrectly() {
        // Act
        budget.setBudgetId(1);

        // Assert
        assertEquals(1, budget.getBudgetId());
    }

    @Test
    void setAndGetTrip_ShouldWorkCorrectly() {
        // Act
        budget.setTrip(trip);

        // Assert
        assertEquals(trip, budget.getTrip());
    }

    @Test
    void setAndGetTotalBudget_ShouldWorkCorrectly() {
        // Act
        budget.setTotalBudget(10000.0);

        // Assert
        assertEquals(10000.0, budget.getTotalBudget());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        // Act
        budget.setCurrency("EUR");

        // Assert
        assertEquals("EUR", budget.getCurrency());
    }

    @Test
    void setAndGetCreatedDate_ShouldWorkCorrectly() {
        // Arrange
        Date testDate = new Date();

        // Act
        budget.setCreatedDate(testDate);

        // Assert
        assertEquals(testDate, budget.getCreatedDate());
    }

    @Test
    void totalBudget_ShouldHandleDecimalValues() {
        // Act
        budget.setTotalBudget(1234.56);

        // Assert
        assertEquals(1234.56, budget.getTotalBudget());
    }

    @Test
    void totalBudget_ShouldHandleZero() {
        // Act
        budget.setTotalBudget(0.0);

        // Assert
        assertEquals(0.0, budget.getTotalBudget());
    }

    @Test
    void totalBudget_ShouldHandleNegativeValues() {
        // Act
        budget.setTotalBudget(-500.0);

        // Assert
        assertEquals(-500.0, budget.getTotalBudget());
    }

    @Test
    void currency_ShouldHandleDifferentCodes() {
        // Arrange
        String[] currencies = {"USD", "EUR", "GBP", "JPY"};

        for (String curr : currencies) {
            // Act
            budget.setCurrency(curr);

            // Assert
            assertEquals(curr, budget.getCurrency());
        }
    }
}
