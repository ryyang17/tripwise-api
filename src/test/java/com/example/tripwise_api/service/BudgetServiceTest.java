package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.BudgetDAL;
import com.example.tripwise_api.model.Budget;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetDAL budgetDAL;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private BudgetService budgetService;

    private Budget validBudget;
    private Trip validTrip;

    @BeforeEach
    void setUp() {
        validTrip = new Trip();
        validTrip.setTripId(1);
        validTrip.setTripName("Test Trip");

        validBudget = new Budget();
        validBudget.setBudgetId(1);
        validBudget.setTrip(validTrip);
        validBudget.setTotalBudget(1000.00); // Changed from BigDecimal to double
        validBudget.setCurrency("EUR");
    }

    @Test
    void getAllBudgets_ShouldReturnAllBudgets() {
        // Arrange
        List<Budget> expectedBudgets = List.of(validBudget);
        when(budgetDAL.getAllBudgets()).thenReturn(expectedBudgets);

        // Act
        List<Budget> result = budgetService.getAllBudgets();

        // Assert
        assertEquals(expectedBudgets, result);
        verify(budgetDAL, times(1)).getAllBudgets();
    }

    @Test
    void getAllBudgets_WhenNoBudgets_ShouldReturnEmptyList() {
        // Arrange
        when(budgetDAL.getAllBudgets()).thenReturn(Collections.emptyList());

        // Act
        List<Budget> result = budgetService.getAllBudgets();

        // Assert
        assertTrue(result.isEmpty());
        verify(budgetDAL, times(1)).getAllBudgets();
    }

    @Test
    void getBudgetById_WhenBudgetExists_ShouldReturnBudget() {
        // Arrange
        when(budgetDAL.getBudgetById(1)).thenReturn(Optional.of(validBudget));

        // Act
        Optional<Budget> result = budgetService.getBudgetById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validBudget, result.get());
        verify(budgetDAL, times(1)).getBudgetById(1);
    }

    @Test
    void getBudgetById_WhenBudgetDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(budgetDAL.getBudgetById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Budget> result = budgetService.getBudgetById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(budgetDAL, times(1)).getBudgetById(999);
    }

    @Test
    void getBudgetByTripId_WhenBudgetExists_ShouldReturnMostRecentBudget() {
        // Arrange
        Budget olderBudget = new Budget();
        olderBudget.setBudgetId(1);
        olderBudget.setTotalBudget(800.00);

        Budget newerBudget = new Budget();
        newerBudget.setBudgetId(2);
        newerBudget.setTotalBudget(1200.00);

        List<Budget> budgets = List.of(olderBudget, newerBudget);
        when(budgetDAL.getBudgetsByTripId(1)).thenReturn(budgets);

        // Act
        Budget result = budgetService.getBudgetByTripId(1);

        // Assert
        assertNotNull(result);
        assertEquals(newerBudget, result);
        assertEquals(1200.00, result.getTotalBudget());
        verify(budgetDAL, times(1)).getBudgetsByTripId(1);
    }

    @Test
    void getBudgetByTripId_WhenNoBudgetExists_ShouldReturnNull() {
        // Arrange
        when(budgetDAL.getBudgetsByTripId(999)).thenReturn(Collections.emptyList());

        // Act
        Budget result = budgetService.getBudgetByTripId(999);

        // Assert
        assertNull(result);
        verify(budgetDAL, times(1)).getBudgetsByTripId(999);
    }

    @Test
    void createBudget_WithValidBudget_ShouldCreateBudget() {
        // Arrange
        when(tripRepository.findById(1)).thenReturn(Optional.of(validTrip));
        when(budgetDAL.getBudgetsByTripId(1)).thenReturn(Collections.emptyList());
        when(budgetDAL.saveBudget(any(Budget.class))).thenReturn(validBudget);

        // Act
        Budget result = budgetService.createBudget(1, validBudget);

        // Assert
        assertEquals(validBudget, result);
        verify(tripRepository, times(1)).findById(1);
        verify(budgetDAL, times(1)).getBudgetsByTripId(1);
        verify(budgetDAL, times(1)).saveBudget(any(Budget.class));
    }

    @Test
    void createBudget_WithExistingBudgets_ShouldDeleteOldBudgetsAndCreateNew() {
        // Arrange
        Budget existingBudget1 = new Budget();
        existingBudget1.setBudgetId(10);
        Budget existingBudget2 = new Budget();
        existingBudget2.setBudgetId(11);

        List<Budget> existingBudgets = List.of(existingBudget1, existingBudget2);

        when(tripRepository.findById(1)).thenReturn(Optional.of(validTrip));
        when(budgetDAL.getBudgetsByTripId(1)).thenReturn(existingBudgets);
        when(budgetDAL.saveBudget(any(Budget.class))).thenReturn(validBudget);

        // Act
        Budget result = budgetService.createBudget(1, validBudget);

        // Assert
        assertEquals(validBudget, result);
        verify(tripRepository, times(1)).findById(1);
        verify(budgetDAL, times(1)).getBudgetsByTripId(1);
        verify(budgetDAL, times(1)).deleteBudget(10);
        verify(budgetDAL, times(1)).deleteBudget(11);
        verify(budgetDAL, times(1)).saveBudget(any(Budget.class));
    }

    @Test
    void createBudget_WithNonExistentTrip_ShouldThrowException() {
        // Arrange
        when(tripRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.createBudget(999, validBudget)
        );
        assertEquals("Trip not found with id: 999", exception.getMessage());
        verify(tripRepository, times(1)).findById(999);
        verify(budgetDAL, never()).saveBudget(any(Budget.class));
    }

    @Test
    void updateOrCreateBudget_WithExistingBudget_ShouldUpdateMostRecent() {
        // Arrange
        Budget olderBudget = new Budget();
        olderBudget.setBudgetId(1);
        olderBudget.setTotalBudget(800.00);

        Budget newerBudget = new Budget();
        newerBudget.setBudgetId(2);
        newerBudget.setTotalBudget(900.00);

        List<Budget> existingBudgets = List.of(olderBudget, newerBudget);

        Budget updateDetails = new Budget();
        updateDetails.setTotalBudget(1500.00);
        updateDetails.setCurrency("USD");

        when(tripRepository.findById(1)).thenReturn(Optional.of(validTrip));
        when(budgetDAL.getBudgetsByTripId(1)).thenReturn(existingBudgets);
        when(budgetDAL.saveBudget(any(Budget.class))).thenReturn(newerBudget);

        // Act
        Budget result = budgetService.updateOrCreateBudget(1, updateDetails);

        // Assert
        assertNotNull(result);
        verify(tripRepository, times(1)).findById(1);
        verify(budgetDAL, times(1)).getBudgetsByTripId(1);
        verify(budgetDAL, times(1)).deleteBudget(1); // Delete older budget
        verify(budgetDAL, times(1)).saveBudget(newerBudget);
        assertEquals(1500.00, newerBudget.getTotalBudget());
        assertEquals("USD", newerBudget.getCurrency());
    }

    @Test
    void updateOrCreateBudget_WithNoBudget_ShouldCreateNewBudget() {
        // Arrange
        Budget newBudgetDetails = new Budget();
        newBudgetDetails.setTotalBudget(2000.00);
        newBudgetDetails.setCurrency("GBP");

        when(tripRepository.findById(1)).thenReturn(Optional.of(validTrip));
        when(budgetDAL.getBudgetsByTripId(1)).thenReturn(Collections.emptyList());
        when(budgetDAL.saveBudget(any(Budget.class))).thenReturn(validBudget);

        // Act
        Budget result = budgetService.updateOrCreateBudget(1, newBudgetDetails);

        // Assert
        assertNotNull(result);
        verify(tripRepository, times(1)).findById(1);
        verify(budgetDAL, times(1)).getBudgetsByTripId(1);
        verify(budgetDAL, never()).deleteBudget(anyInt());
        verify(budgetDAL, times(1)).saveBudget(any(Budget.class));
    }

    @Test
    void updateOrCreateBudget_WithNonExistentTrip_ShouldThrowException() {
        // Arrange
        when(tripRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.updateOrCreateBudget(999, validBudget)
        );
        assertEquals("Trip not found with id: 999", exception.getMessage());
        verify(tripRepository, times(1)).findById(999);
        verify(budgetDAL, never()).saveBudget(any(Budget.class));
    }

    @Test
    void deleteBudget_WithExistingBudgets_ShouldDeleteAllBudgets() {
        // Arrange
        Budget budget1 = new Budget();
        budget1.setBudgetId(1);
        Budget budget2 = new Budget();
        budget2.setBudgetId(2);

        List<Budget> budgets = List.of(budget1, budget2);
        when(budgetDAL.getBudgetsByTripId(1)).thenReturn(budgets);

        // Act
        budgetService.deleteBudget(1);

        // Assert
        verify(budgetDAL, times(1)).getBudgetsByTripId(1);
        verify(budgetDAL, times(1)).deleteBudget(1);
        verify(budgetDAL, times(1)).deleteBudget(2);
    }

    @Test
    void deleteBudget_WithNoBudgets_ShouldThrowException() {
        // Arrange
        when(budgetDAL.getBudgetsByTripId(999)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> budgetService.deleteBudget(999)
        );
        assertEquals("No budget found for trip with id: 999", exception.getMessage());
        verify(budgetDAL, times(1)).getBudgetsByTripId(999);
        verify(budgetDAL, never()).deleteBudget(anyInt());
    }
}
