package com.example.tripwise_api.controller;

import com.example.tripwise_api.model.Budget;
import com.example.tripwise_api.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BudgetController budgetController;

    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testBudget = new Budget();
        testBudget.setBudgetId(1);
        testBudget.setTotalBudget(1000.00);
        testBudget.setCurrency("USD");
    }

    @Test
    void getBudgetByTripId_WhenExists_ShouldReturnBudget() {
        // Arrange
        when(budgetService.getBudgetByTripId(1)).thenReturn(testBudget);

        // Act
        ResponseEntity<Budget> response = budgetController.getBudgetByTripId(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testBudget, response.getBody());
        verify(budgetService, times(1)).getBudgetByTripId(1);
    }

    @Test
    void getBudgetByTripId_WhenNotExists_ShouldReturnNotFound() {
        // Arrange
        when(budgetService.getBudgetByTripId(999)).thenReturn(null);

        // Act
        ResponseEntity<Budget> response = budgetController.getBudgetByTripId(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(budgetService, times(1)).getBudgetByTripId(999);
    }

    @Test
    void createBudget_WithValidData_ShouldReturnCreated() {
        // Arrange
        when(budgetService.createBudget(eq(1), any(Budget.class))).thenReturn(testBudget);

        // Act
        ResponseEntity<Budget> response = budgetController.createBudget(1, testBudget);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testBudget, response.getBody());
        verify(budgetService, times(1)).createBudget(eq(1), any(Budget.class));
    }

    @Test
    void createBudget_WithInvalidData_ShouldReturnBadRequest() {
        // Arrange
        when(budgetService.createBudget(eq(1), any(Budget.class)))
            .thenThrow(new IllegalArgumentException("Invalid budget"));

        // Act
        ResponseEntity<Budget> response = budgetController.createBudget(1, testBudget);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(budgetService, times(1)).createBudget(eq(1), any(Budget.class));
    }

    @Test
    void updateOrCreateBudget_WithValidData_ShouldReturnOk() {
        // Arrange
        when(budgetService.updateOrCreateBudget(eq(1), any(Budget.class))).thenReturn(testBudget);

        // Act
        ResponseEntity<Budget> response = budgetController.updateOrCreateBudget(1, testBudget);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testBudget, response.getBody());
        verify(budgetService, times(1)).updateOrCreateBudget(eq(1), any(Budget.class));
    }

    @Test
    void updateOrCreateBudget_WhenTripNotFound_ShouldReturnNotFound() {
        // Arrange
        when(budgetService.updateOrCreateBudget(eq(999), any(Budget.class)))
            .thenThrow(new IllegalArgumentException("Trip not found"));

        // Act
        ResponseEntity<Budget> response = budgetController.updateOrCreateBudget(999, testBudget);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(budgetService, times(1)).updateOrCreateBudget(eq(999), any(Budget.class));
    }

    @Test
    void deleteBudget_WhenExists_ShouldReturnOk() {
        // Arrange
        doNothing().when(budgetService).deleteBudget(1);

        // Act
        ResponseEntity<String> response = budgetController.deleteBudget(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Budget deleted successfully", response.getBody());
        verify(budgetService, times(1)).deleteBudget(1);
    }

    @Test
    void deleteBudget_WhenNotExists_ShouldReturnNotFound() {
        // Arrange
        doThrow(new IllegalArgumentException("Budget not found"))
            .when(budgetService).deleteBudget(999);

        // Act
        ResponseEntity<String> response = budgetController.deleteBudget(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Budget not found", response.getBody());
        verify(budgetService, times(1)).deleteBudget(999);
    }
}

