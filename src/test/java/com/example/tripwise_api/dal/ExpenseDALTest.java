package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.Expense;
import com.example.tripwise_api.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseDALTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseDAL expenseDAL;

    private Expense testExpense;

    @BeforeEach
    void setUp() {
        testExpense = new Expense();
        testExpense.setExpenseId(1);
        testExpense.setTripId(1);
        testExpense.setDescription("Lunch");
        testExpense.setAmount(new BigDecimal("50.00"));
        testExpense.setCurrency("USD");
        testExpense.setExpenseDate(LocalDate.now());
    }

    @Test
    void getAllExpenses_ShouldReturnAllExpenses() {
        // Arrange
        List<Expense> expectedExpenses = List.of(testExpense);
        when(expenseRepository.findAll()).thenReturn(expectedExpenses);

        // Act
        List<Expense> result = expenseDAL.getAllExpenses();

        // Assert
        assertEquals(expectedExpenses, result);
        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    void getExpenseById_WhenExists_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findById(1)).thenReturn(Optional.of(testExpense));

        // Act
        Optional<Expense> result = expenseDAL.getExpenseById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testExpense, result.get());
        verify(expenseRepository, times(1)).findById(1);
    }

    @Test
    void getExpenseById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(expenseRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Expense> result = expenseDAL.getExpenseById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(expenseRepository, times(1)).findById(999);
    }

    @Test
    void getExpensesByTripId_ShouldReturnTripExpenses() {
        // Arrange
        List<Expense> expectedExpenses = List.of(testExpense);
        when(expenseRepository.findByTripId(1)).thenReturn(expectedExpenses);

        // Act
        List<Expense> result = expenseDAL.getExpensesByTripId(1);

        // Assert
        assertEquals(expectedExpenses, result);
        verify(expenseRepository, times(1)).findByTripId(1);
    }

    @Test
    void saveExpense_ShouldSaveAndReturnExpense() {
        // Arrange
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        Expense result = expenseDAL.saveExpense(testExpense);


        // Assert
        assertNotNull(result);
        assertEquals(testExpense, result);
        verify(expenseRepository, times(1)).save(testExpense);
    }

    @Test
    void deleteExpense_ShouldDeleteExpense() {
        // Arrange
        doNothing().when(expenseRepository).deleteById(1);

        // Act
        expenseDAL.deleteExpense(1);

        // Assert
        verify(expenseRepository, times(1)).deleteById(1);
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(expenseRepository.existsById(1)).thenReturn(true);

        // Act
        boolean result = expenseDAL.existsById(1);

        // Assert
        assertTrue(result);
        verify(expenseRepository, times(1)).existsById(1);
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(expenseRepository.existsById(999)).thenReturn(false);

        // Act
        boolean result = expenseDAL.existsById(999);

        // Assert
        assertFalse(result);
        verify(expenseRepository, times(1)).existsById(999);
    }
}

