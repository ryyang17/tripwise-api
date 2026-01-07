package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.Budget;
import com.example.tripwise_api.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetDALTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetDAL budgetDAL;

    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testBudget = new Budget();
        testBudget.setBudgetId(1);
        testBudget.setTotalBudget(1000.00);
        testBudget.setCurrency("USD");
    }

    @Test
    void getAllBudgets_ShouldReturnAllBudgets() {
        // Arrange
        List<Budget> expectedBudgets = List.of(testBudget);
        when(budgetRepository.findAll()).thenReturn(expectedBudgets);

        // Act
        List<Budget> result = budgetDAL.getAllBudgets();

        // Assert
        assertEquals(expectedBudgets, result);
        verify(budgetRepository, times(1)).findAll();
    }

    @Test
    void getBudgetById_WhenExists_ShouldReturnBudget() {
        // Arrange
        when(budgetRepository.findById(1)).thenReturn(Optional.of(testBudget));

        // Act
        Optional<Budget> result = budgetDAL.getBudgetById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBudget, result.get());
        verify(budgetRepository, times(1)).findById(1);
    }

    @Test
    void getBudgetById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(budgetRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Budget> result = budgetDAL.getBudgetById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(budgetRepository, times(1)).findById(999);
    }

    @Test
    void getBudgetsByTripId_ShouldReturnBudgets() {
        // Arrange
        List<Budget> expectedBudgets = List.of(testBudget);
        when(budgetRepository.findByTrip_TripId(1)).thenReturn(expectedBudgets);

        // Act
        List<Budget> result = budgetDAL.getBudgetsByTripId(1);

        // Assert
        assertEquals(expectedBudgets, result);
        verify(budgetRepository, times(1)).findByTrip_TripId(1);
    }

    @Test
    void saveBudget_ShouldSaveAndReturnBudget() {
        // Arrange
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);

        // Act
        Budget result = budgetDAL.saveBudget(testBudget);


        // Assert
        assertNotNull(result);
        assertEquals(testBudget, result);
        verify(budgetRepository, times(1)).save(testBudget);
    }

    @Test
    void deleteBudget_ShouldDeleteBudget() {
        // Arrange
        doNothing().when(budgetRepository).deleteById(1);

        // Act
        budgetDAL.deleteBudget(1);

        // Assert
        verify(budgetRepository, times(1)).deleteById(1);
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(budgetRepository.existsById(1)).thenReturn(true);

        // Act
        boolean result = budgetDAL.existsById(1);

        // Assert
        assertTrue(result);
        verify(budgetRepository, times(1)).existsById(1);
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(budgetRepository.existsById(999)).thenReturn(false);

        // Act
        boolean result = budgetDAL.existsById(999);

        // Assert
        assertFalse(result);
        verify(budgetRepository, times(1)).existsById(999);
    }
}

