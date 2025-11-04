package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.ExpenseDAL;
import com.example.tripwise_api.dal.ExpenseShareDAL;
import com.example.tripwise_api.dto.request.SharedExpenseRequest;
import com.example.tripwise_api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseDAL expenseDAL;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private ExpenseShareDAL expenseShareDAL;

    @Mock
    private ExpenseCategoryService expenseCategoryService;

    @Mock
    private TripParticipantService tripParticipantService;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense validExpense;
    private ExpenseCategory category;
    private TripParticipant participant;

    @BeforeEach
    void setUp() {
        category = new ExpenseCategory();
        category.setCategoryId(1L);
        category.setCategoryName("Food");

        participant = new TripParticipant();
        participant.setId(1); // Changed from setParticipantId to setId
        participant.setTripId(1);
        participant.setUserId(100L);

        validExpense = new Expense();
        validExpense.setExpenseId(1);
        validExpense.setTripId(1);
        validExpense.setDescription("Test Expense");
        validExpense.setAmount(new BigDecimal("50.00"));
        validExpense.setOriginalAmount(new BigDecimal("50.00"));
        validExpense.setCurrency("EUR");
        validExpense.setExchangeRate(BigDecimal.ONE);
        validExpense.setCategory(category);
        validExpense.setExpenseDate(LocalDate.now());
        validExpense.setPaidBy(100L);
        validExpense.setValidated(false);
    }

    @Test
    void getAllExpenses_ShouldReturnAllExpenses() {
        // Arrange
        List<Expense> expectedExpenses = List.of(validExpense);
        when(expenseDAL.getAllExpenses()).thenReturn(expectedExpenses);

        // Act
        List<Expense> result = expenseService.getAllExpenses();

        // Assert
        assertEquals(expectedExpenses, result);
        verify(expenseDAL, times(1)).getAllExpenses();
    }

    @Test
    void getExpensesByTripId_ShouldReturnTripExpenses() {
        // Arrange
        List<Expense> expectedExpenses = List.of(validExpense);
        when(expenseDAL.getExpensesByTripId(1)).thenReturn(expectedExpenses);

        // Act
        List<Expense> result = expenseService.getExpensesByTripId(1);

        // Assert
        assertEquals(expectedExpenses, result);
        verify(expenseDAL, times(1)).getExpensesByTripId(1);
    }

    @Test
    void getExpensesByTripIdWithCurrency_WithCurrencyConversion_ShouldReturnConvertedExpenses() {
        // Arrange
        List<Expense> expenses = List.of(validExpense);
        when(expenseDAL.getExpensesByTripId(1)).thenReturn(expenses);
        when(currencyService.getExchangeRate("EUR", "USD")).thenReturn(new BigDecimal("1.2"));

        // Act
        List<Expense> result = expenseService.getExpensesByTripIdWithCurrency(1, "USD");

        // Assert
        assertEquals(1, result.size());
        verify(expenseDAL, times(1)).getExpensesByTripId(1);
        verify(currencyService, times(1)).getExchangeRate("EUR", "USD");
    }

    @Test
    void getExpensesByTripIdWithCurrency_WithNullCurrency_ShouldReturnOriginalExpenses() {
        // Arrange
        List<Expense> expenses = List.of(validExpense);
        when(expenseDAL.getExpensesByTripId(1)).thenReturn(expenses);

        // Act
        List<Expense> result = expenseService.getExpensesByTripIdWithCurrency(1, null);

        // Assert
        assertEquals(expenses, result);
        verify(expenseDAL, times(1)).getExpensesByTripId(1);
        verify(currencyService, never()).getExchangeRate(any(), any());
    }

    @Test
    void getExpenseById_WhenExpenseExists_ShouldReturnExpense() {
        // Arrange
        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(validExpense));

        // Act
        Optional<Expense> result = expenseService.getExpenseById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validExpense, result.get());
        verify(expenseDAL, times(1)).getExpenseById(1);
    }

    @Test
    void getExpenseById_WhenExpenseDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(expenseDAL.getExpenseById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Expense> result = expenseService.getExpenseById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(expenseDAL, times(1)).getExpenseById(999);
    }

    @Test
    void createExpense_WithValidExpense_ShouldCreateExpense() {
        // Arrange
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createExpense(1, validExpense);

        // Assert
        assertEquals(validExpense, result);
        verify(expenseDAL, times(1)).saveExpense(any(Expense.class));
    }

    @Test
    void createExpenseWithCurrency_WithCurrencyConversion_ShouldCreateConvertedExpense() {
        // Arrange
        when(currencyService.getExchangeRate("EUR", "USD")).thenReturn(new BigDecimal("1.2"));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createExpenseWithCurrency(1, validExpense, "USD");

        // Assert
        assertNotNull(result);
        verify(currencyService, times(1)).getExchangeRate("EUR", "USD");
        verify(expenseDAL, times(1)).saveExpense(any(Expense.class));
    }

    @Test
    void createExpenseWithCurrency_WithSameCurrency_ShouldNotConvert() {
        // Arrange
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createExpenseWithCurrency(1, validExpense, "EUR");

        // Assert
        assertNotNull(result);
        verify(currencyService, never()).getExchangeRate(any(), any());
        verify(expenseDAL, times(1)).saveExpense(any(Expense.class));
    }

    @Test
    void updateExpense_WhenExpenseExists_ShouldUpdateExpense() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setDescription("Old Description");

        Expense updateDetails = new Expense();
        updateDetails.setDescription("New Description");
        updateDetails.setAmount(new BigDecimal("75.00"));
        updateDetails.setCategory(category);
        updateDetails.setExpenseDate(LocalDate.now());
        updateDetails.setValidated(true);

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);

        // Act
        Expense result = expenseService.updateExpense(1, updateDetails);

        // Assert
        assertNotNull(result);
        verify(expenseDAL, times(1)).getExpenseById(1);
        verify(expenseDAL, times(1)).saveExpense(existingExpense);
        assertEquals("New Description", existingExpense.getDescription());
    }

    @Test
    void updateExpense_WhenExpenseDoesNotExist_ShouldThrowException() {
        // Arrange
        when(expenseDAL.getExpenseById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateExpense(999, validExpense)
        );
        assertEquals("Expense not found with id: 999", exception.getMessage());
        verify(expenseDAL, times(1)).getExpenseById(999);
        verify(expenseDAL, never()).saveExpense(any(Expense.class));
    }

    @Test
    void deleteExpense_WhenExpenseExists_ShouldDeleteExpense() {
        // Arrange
        when(expenseDAL.existsById(1)).thenReturn(true);

        // Act
        expenseService.deleteExpense(1);

        // Assert
        verify(expenseDAL, times(1)).existsById(1);
        verify(expenseShareDAL, times(1)).deleteSharesByExpenseId(1);
        verify(expenseDAL, times(1)).deleteExpense(1);
    }

    @Test
    void deleteExpense_WhenExpenseDoesNotExist_ShouldThrowException() {
        // Arrange
        when(expenseDAL.existsById(999)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.deleteExpense(999)
        );
        assertEquals("Expense not found with id: 999", exception.getMessage());
        verify(expenseDAL, times(1)).existsById(999);
        verify(expenseDAL, never()).deleteExpense(999);
    }

    @Test
    void createSharedExpense_WithValidRequest_ShouldCreateSharedExpense() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Shared Dinner");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("EUR");
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1)); // Changed from List.of(1, 2) to just List.of(1)
        request.setSplitEvenly(true);
        request.setExpenseDate(LocalDate.now());

        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createSharedExpense(1, request);

        // Assert
        assertNotNull(result);
        verify(expenseCategoryService, times(1)).getCategoryById(1L);
        verify(tripParticipantService, times(2)).getParticipantById(1);
        // Changed from times(1) to times(2) since it's called for payer validation and participant validation
        verify(expenseDAL, times(1)).saveExpense(any(Expense.class));
    }

    @Test
    void createSharedExpense_WithEmptyDescription_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("");
        request.setAmount(new BigDecimal("100.00"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    void createSharedExpense_WithZeroAmount_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(BigDecimal.ZERO);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void createSharedExpense_WithNegativeAmount_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("-10.00"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void createSharedExpense_WithEmptyParticipants_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setSplitWithParticipantIds(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertEquals("Split participants are required", exception.getMessage());
    }

    @Test
    void createSharedExpense_WithInvalidCategory_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(999);
        request.setSplitWithParticipantIds(List.of(1)); // Changed from List.of(1, 2) to List.of(1)

        when(expenseCategoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertEquals("Category not found with id: 999", exception.getMessage());
    }

    @Test
    void createSharedExpense_WithInvalidPayer_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(999);
        request.setSplitWithParticipantIds(List.of(1)); // Changed from List.of(1, 2) to List.of(1)

        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertEquals("Payer participant not found with id: 999", exception.getMessage());
    }

    @Test
    void createSharedExpense_WithPayerFromDifferentTrip_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1)); // Changed from List.of(1, 2) to List.of(1)

        TripParticipant wrongTripParticipant = new TripParticipant();
        wrongTripParticipant.setId(1); // Changed from setParticipantId to setId
        wrongTripParticipant.setTripId(999); // Different trip
        wrongTripParticipant.setUserId(100L);

        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(wrongTripParticipant));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertEquals("Payer participant does not belong to this trip", exception.getMessage());
    }

    @Test
    void createSharedExpense_WithInvalidParticipantInSplit_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1, 999)); // 999 doesn't exist

        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(tripParticipantService.getParticipantById(999)).thenReturn(Optional.empty());
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertEquals("Participant not found with id: 999", exception.getMessage());
    }

    @Test
    void createSharedExpense_WithParticipantFromDifferentTrip_ShouldThrowException() {
        // Arrange
        TripParticipant wrongTripParticipant = new TripParticipant();
        wrongTripParticipant.setId(2);
        wrongTripParticipant.setTripId(999); // Different trip
        wrongTripParticipant.setUserId(200L);

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1, 2));

        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(tripParticipantService.getParticipantById(2)).thenReturn(Optional.of(wrongTripParticipant));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense); // Ensure non-null Expense

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertEquals("Participant 2 does not belong to this trip", exception.getMessage());
    }

    @Test
    void createSharedExpense_WithCustomSplitsNotAddingUp_ShouldThrowException() {
        // Arrange
        Map<String, BigDecimal> customSplits = new HashMap<>();
        customSplits.put("1", new BigDecimal("30.00")); // Only 30 out of 100

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Custom Split Expense");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1));
        request.setSplitEvenly(false);
        request.setCustomSplits(customSplits);

        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense); // Ensure non-null Expense

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(1, request)
        );
        assertTrue(exception.getMessage().contains("Custom split amounts"));
        assertTrue(exception.getMessage().contains("do not add up to total expense amount"));
    }

    @Test
    void updateSharedExpense_WithValidRequest_ShouldUpdateExpense() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);
        existingExpense.setDescription("Old Description");
        existingExpense.setAmount(new BigDecimal("50.00"));

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Updated Shared Dinner");
        request.setAmount(new BigDecimal("150.00"));
        request.setCurrency("USD");
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1));
        request.setSplitEvenly(true);
        request.setExpenseDate(LocalDate.now().plusDays(1));

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);

        // Act
        Expense result = expenseService.updateSharedExpense(1, 1, request);

        // Assert
        assertNotNull(result);
        verify(expenseDAL, times(1)).getExpenseById(1);
        verify(expenseCategoryService, times(1)).getCategoryById(1L);
        verify(tripParticipantService, times(2)).getParticipantById(1); // Called twice
        verify(expenseShareDAL, times(1)).deleteSharesByExpenseId(1);
        verify(expenseDAL, times(1)).saveExpense(existingExpense);
        assertEquals("Updated Shared Dinner", existingExpense.getDescription());
        assertEquals(new BigDecimal("150.00"), existingExpense.getAmount());
        assertEquals("USD", existingExpense.getCurrency());
    }

    @Test
    void updateSharedExpense_WithNonExistentExpense_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setSplitWithParticipantIds(List.of(1));

        when(expenseDAL.getExpenseById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 999, request)
        );
        assertEquals("Expense not found with id: 999", exception.getMessage());
        verify(expenseDAL, times(1)).getExpenseById(999);
        verify(expenseDAL, never()).saveExpense(any(Expense.class));
    }

    @Test
    void updateSharedExpense_WithExpenseFromDifferentTrip_ShouldThrowException() {
        // Arrange
        Expense expenseFromDifferentTrip = new Expense();
        expenseFromDifferentTrip.setExpenseId(1);
        expenseFromDifferentTrip.setTripId(999); // Different trip

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setSplitWithParticipantIds(List.of(1));

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(expenseFromDifferentTrip));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertEquals("Expense does not belong to this trip", exception.getMessage());
        verify(expenseDAL, times(1)).getExpenseById(1);
        verify(expenseDAL, never()).saveExpense(any(Expense.class));
    }

    @Test
    void updateSharedExpense_WithEmptyDescription_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("");
        request.setAmount(new BigDecimal("100.00"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    void updateSharedExpense_WithZeroAmount_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(BigDecimal.ZERO);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void updateSharedExpense_WithEmptyParticipants_ShouldThrowException() {
        // Arrange
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setSplitWithParticipantIds(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertEquals("Split participants are required", exception.getMessage());
    }

    @Test
    void updateSharedExpense_WithInvalidCategory_ShouldThrowException() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(999);
        request.setSplitWithParticipantIds(List.of(1));

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertEquals("Category not found with id: 999", exception.getMessage());
    }

    @Test
    void updateSharedExpense_WithInvalidPayer_ShouldThrowException() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(999);
        request.setSplitWithParticipantIds(List.of(1));

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertEquals("Payer participant not found with id: 999", exception.getMessage());
    }

    @Test
    void updateSharedExpense_WithPayerFromDifferentTrip_ShouldThrowException() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);

        TripParticipant wrongTripParticipant = new TripParticipant();
        wrongTripParticipant.setId(1);
        wrongTripParticipant.setTripId(999); // Different trip
        wrongTripParticipant.setUserId(100L);

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1));

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(wrongTripParticipant));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertEquals("Payer participant does not belong to this trip", exception.getMessage());
    }

    @Test
    void updateSharedExpense_WithCustomSplits_ShouldHandleCustomSplits() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);

        Map<String, BigDecimal> customSplits = new HashMap<>();
        customSplits.put("1", new BigDecimal("60.00"));
        customSplits.put("2", new BigDecimal("40.00")); // Add up to 100

        TripParticipant participant2 = new TripParticipant();
        participant2.setId(2);
        participant2.setTripId(1);
        participant2.setUserId(200L);

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Custom Split Expense");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1, 2));
        request.setSplitEvenly(false);
        request.setCustomSplits(customSplits);

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(tripParticipantService.getParticipantById(2)).thenReturn(Optional.of(participant2));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);


        // Act
        Expense result = expenseService.updateSharedExpense(1, 1, request);

        // Assert
        assertNotNull(result);
        verify(expenseDAL, times(1)).getExpenseById(1);
        verify(expenseShareDAL, times(1)).deleteSharesByExpenseId(1);
        verify(expenseDAL, times(1)).saveExpense(existingExpense);
    }

    @Test
    void updateSharedExpense_WithNullExpenseDate_ShouldNotSetDate() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);
        existingExpense.setExpenseDate(LocalDate.now().minusDays(1));

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1));
        request.setExpenseDate(null); // Null date

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);

        LocalDate originalDate = existingExpense.getExpenseDate();

        // Act
        Expense result = expenseService.updateSharedExpense(1, 1, request);

        // Assert
        assertNotNull(result);
        assertEquals(originalDate, existingExpense.getExpenseDate()); // Date should remain unchanged
        verify(expenseDAL, times(1)).saveExpense(existingExpense);
    }

    @Test
    void updateSharedExpense_WithNullCurrency_ShouldDefaultToEUR() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1));
        request.setCurrency(null); // Null currency

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);

        // Act
        Expense result = expenseService.updateSharedExpense(1, 1, request);

        // Assert
        assertNotNull(result);
        assertEquals("EUR", existingExpense.getCurrency()); // Should default to EUR
        verify(expenseDAL, times(1)).saveExpense(existingExpense);
    }

    @Test
    void updateSharedExpense_WithInvalidParticipantInSplit_ShouldThrowException() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1, 999)); // 999 doesn't exist

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(tripParticipantService.getParticipantById(999)).thenReturn(Optional.empty());
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertEquals("Participant not found with id: 999", exception.getMessage());
    }

    @Test
    void updateSharedExpense_WithParticipantFromDifferentTrip_ShouldThrowException() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);

        TripParticipant wrongTripParticipant = new TripParticipant();
        wrongTripParticipant.setId(2);
        wrongTripParticipant.setTripId(999); // Different trip
        wrongTripParticipant.setUserId(200L);

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Valid Description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1, 2));

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(tripParticipantService.getParticipantById(2)).thenReturn(Optional.of(wrongTripParticipant));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertEquals("Participant 2 does not belong to this trip", exception.getMessage());
    }

    @Test
    void updateSharedExpense_WithCustomSplitsNotAddingUp_ShouldThrowException() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setTripId(1);

        Map<String, BigDecimal> customSplits = new HashMap<>();
        customSplits.put("1", new BigDecimal("30.00")); // Only 30 out of 100

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Custom Split Expense");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1));
        request.setSplitEvenly(false);
        request.setCustomSplits(customSplits);

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(1, 1, request)
        );
        assertTrue(exception.getMessage().contains("Custom split amounts"));
        assertTrue(exception.getMessage().contains("do not add up to total expense amount"));
    }

    @Test
    void populateSharedExpenseFields_WithExpenseHavingShares_ShouldPopulateFields() {
        // Arrange
        Expense expenseWithShares = new Expense();
        expenseWithShares.setExpenseId(1);
        expenseWithShares.setTripId(1);
        expenseWithShares.setPaidBy(100L);

        ExpenseShare share1 = new ExpenseShare(1, 100L, new BigDecimal("25.00"));
        ExpenseShare share2 = new ExpenseShare(1, 200L, new BigDecimal("25.00"));
        List<ExpenseShare> shares = List.of(share1, share2);

        TripParticipant participant2 = new TripParticipant();
        participant2.setId(2);
        participant2.setTripId(1);
        participant2.setUserId(200L);

        when(expenseDAL.getExpensesByTripId(1)).thenReturn(List.of(expenseWithShares));
        when(expenseShareDAL.getSharesByExpenseId(1)).thenReturn(shares);
        when(tripParticipantService.getParticipantsByTripId(1)).thenReturn(List.of(participant, participant2));

        // Act
        List<Expense> result = expenseService.getExpensesByTripId(1);

        // Assert
        assertEquals(1, result.size());
        Expense expense = result.get(0);
        assertTrue(expense.getIsSharedExpense());
        assertTrue(expense.getSplitEvenly()); // Both shares have same amount
        assertNotNull(expense.getSplitWith());
        assertNotNull(expense.getCustomSplits());
        verify(expenseShareDAL, times(1)).getSharesByExpenseId(1);
        verify(tripParticipantService, times(1)).getParticipantsByTripId(1);
    }

    @Test
    void populateSharedExpenseFields_WithUnevenShares_ShouldSetSplitEvenlyFalse() {
        // Arrange
        Expense expenseWithUnevenShares = new Expense();
        expenseWithUnevenShares.setExpenseId(1);
        expenseWithUnevenShares.setTripId(1);
        expenseWithUnevenShares.setPaidBy(100L);

        ExpenseShare share1 = new ExpenseShare(1, 100L, new BigDecimal("30.00"));
        ExpenseShare share2 = new ExpenseShare(1, 200L, new BigDecimal("20.00")); // Different amount
        List<ExpenseShare> shares = List.of(share1, share2);

        TripParticipant participant2 = new TripParticipant();
        participant2.setId(2);
        participant2.setTripId(1);
        participant2.setUserId(200L);

        when(expenseDAL.getExpensesByTripId(1)).thenReturn(List.of(expenseWithUnevenShares));
        when(expenseShareDAL.getSharesByExpenseId(1)).thenReturn(shares);
        when(tripParticipantService.getParticipantsByTripId(1)).thenReturn(List.of(participant, participant2));

        // Act
        List<Expense> result = expenseService.getExpensesByTripId(1);

        // Assert
        assertEquals(1, result.size());
        Expense expense = result.get(0);
        assertTrue(expense.getIsSharedExpense());
        assertFalse(expense.getSplitEvenly()); // Different amounts
    }

    @Test
    void populateSharedExpenseFields_WithNoShares_ShouldSetSharedExpenseFalse() {
        // Arrange
        Expense expenseWithoutShares = new Expense();
        expenseWithoutShares.setExpenseId(1);
        expenseWithoutShares.setTripId(1);

        when(expenseDAL.getExpensesByTripId(1)).thenReturn(List.of(expenseWithoutShares));
        when(expenseShareDAL.getSharesByExpenseId(1)).thenReturn(Collections.emptyList());

        // Act
        List<Expense> result = expenseService.getExpensesByTripId(1);

        // Assert
        assertEquals(1, result.size());
        Expense expense = result.get(0);
        assertFalse(expense.getIsSharedExpense());
        assertNull(expense.getPaidById());
        assertTrue(expense.getSplitWith().isEmpty());
        assertTrue(expense.getSplitEvenly());
        assertTrue(expense.getCustomSplits().isEmpty());
    }

    @Test
    void createSharedExpense_WithCustomSplitsAndZeroAmount_ShouldSkipZeroShares() {
        // Arrange
        Map<String, BigDecimal> customSplits = new HashMap<>();
        customSplits.put("1", new BigDecimal("50.00"));
        customSplits.put("2", BigDecimal.ZERO); // Zero amount should be skipped

        TripParticipant participant2 = new TripParticipant();
        participant2.setId(2);
        participant2.setTripId(1);
        participant2.setUserId(200L);

        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Custom Split Expense");
        request.setAmount(new BigDecimal("50.00"));
        request.setCategoryId(1);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(List.of(1, 2));
        request.setSplitEvenly(false);
        request.setCustomSplits(customSplits);

        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(tripParticipantService.getParticipantById(1)).thenReturn(Optional.of(participant));
        when(tripParticipantService.getParticipantById(2)).thenReturn(Optional.of(participant2));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createSharedExpense(1, request);

        // Assert
        assertNotNull(result);
        verify(expenseShareDAL, times(1)).saveShare(any(ExpenseShare.class)); // Only 1 share for non-zero amount
    }

    @Test
    void createExpense_WithSplitWithUserIds_ShouldCreateShares() {
        // Arrange
        validExpense.setSplitWithUserIds(List.of(100L, 200L));
        validExpense.setPaidBy(100L);

        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createExpense(1, validExpense);

        // Assert
        assertNotNull(result);
        verify(expenseDAL, times(1)).saveExpense(any(Expense.class));
        verify(expenseShareDAL, times(2)).saveShare(any(ExpenseShare.class));
    }

    @Test
    void updateExpense_WithOriginalAmount_ShouldUpdateOriginalAmount() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setDescription("Old Description");

        Expense updateDetails = new Expense();
        updateDetails.setDescription("New Description");
        updateDetails.setAmount(new BigDecimal("75.00"));
        updateDetails.setOriginalAmount(new BigDecimal("80.00"));
        updateDetails.setCategory(category);
        updateDetails.setExpenseDate(LocalDate.now());
        updateDetails.setValidated(true);

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);

        // Act
        Expense result = expenseService.updateExpense(1, updateDetails);

        // Assert
        assertNotNull(result);
        verify(expenseDAL, times(1)).saveExpense(existingExpense);
        assertEquals(new BigDecimal("80.00"), existingExpense.getOriginalAmount());
    }

    @Test
    void updateExpense_WithPaidBy_ShouldUpdatePaidBy() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setDescription("Old Description");

        Expense updateDetails = new Expense();
        updateDetails.setDescription("New Description");
        updateDetails.setAmount(new BigDecimal("75.00"));
        updateDetails.setCategory(category);
        updateDetails.setExpenseDate(LocalDate.now());
        updateDetails.setValidated(true);
        updateDetails.setPaidBy(200L);

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(existingExpense));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existingExpense);

        // Act
        Expense result = expenseService.updateExpense(1, updateDetails);

        // Assert
        assertNotNull(result);
        verify(expenseDAL, times(1)).saveExpense(existingExpense);
        assertEquals(200L, existingExpense.getPaidBy());
    }

    @Test
    void getExpensesByTripIdWithCurrency_WithEmptyCurrency_ShouldReturnOriginalExpenses() {
        // Arrange
        List<Expense> expenses = List.of(validExpense);
        when(expenseDAL.getExpensesByTripId(1)).thenReturn(expenses);

        // Act
        List<Expense> result = expenseService.getExpensesByTripIdWithCurrency(1, "");

        // Assert
        assertEquals(expenses, result);
        verify(expenseDAL, times(1)).getExpensesByTripId(1);
        verify(currencyService, never()).getExchangeRate(any(), any());
    }

    @Test
    void getExpensesByTripIdWithCurrency_WithSameCurrency_ShouldNotConvert() {
        // Arrange
        List<Expense> expenses = List.of(validExpense);
        when(expenseDAL.getExpensesByTripId(1)).thenReturn(expenses);

        // Act
        List<Expense> result = expenseService.getExpensesByTripIdWithCurrency(1, "EUR");

        // Assert
        assertEquals(1, result.size());
        verify(expenseDAL, times(1)).getExpensesByTripId(1);
        verify(currencyService, never()).getExchangeRate(any(), any());
    }

    @Test
    void createExpenseWithCurrency_WithNullCurrency_ShouldDefaultToEUR() {
        // Arrange
        validExpense.setCurrency(null);
        when(currencyService.getExchangeRate("EUR", "USD")).thenReturn(new BigDecimal("1.2"));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createExpenseWithCurrency(1, validExpense, "USD");

        // Assert
        assertNotNull(result);
        verify(currencyService, times(1)).getExchangeRate("EUR", "USD");
        verify(expenseDAL, times(1)).saveExpense(any(Expense.class));
    }

    @Test
    void createExpense_WithNullCurrency_ShouldDefaultToEUR() {
        // Arrange
        validExpense.setCurrency(null);
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createExpense(1, validExpense);

        // Assert
        assertNotNull(result);
        verify(expenseDAL, times(1)).saveExpense(any(Expense.class));
    }

    @Test
    void createExpense_WithNullOriginalAmount_ShouldUseAmount() {
        // Arrange
        validExpense.setOriginalAmount(null);
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(validExpense);

        // Act
        Expense result = expenseService.createExpense(1, validExpense);

        // Assert
        assertNotNull(result);
        verify(expenseDAL, times(1)).saveExpense(any(Expense.class));
    }

    @Test
    void populateSharedExpenseFields_WithNullShares_ShouldSetDefaults() {
        // Arrange
        Expense expense = new Expense();
        expense.setExpenseId(1);
        expense.setTripId(1);

        when(expenseDAL.getExpensesByTripId(1)).thenReturn(List.of(expense));
        when(expenseShareDAL.getSharesByExpenseId(1)).thenReturn(null);

        // Act
        List<Expense> result = expenseService.getExpensesByTripId(1);

        // Assert
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsSharedExpense());
    }

    @Test
    void populateSharedExpenseFields_WithParticipantNotInMap_ShouldSkipParticipant() {
        // Arrange
        Expense expense = new Expense();
        expense.setExpenseId(1);
        expense.setTripId(1);
        expense.setPaidBy(999L); // User not in trip

        ExpenseShare share = new ExpenseShare(1, 999L, new BigDecimal("50.00"));

        when(expenseDAL.getExpensesByTripId(1)).thenReturn(List.of(expense));
        when(expenseShareDAL.getSharesByExpenseId(1)).thenReturn(List.of(share));
        when(tripParticipantService.getParticipantsByTripId(1)).thenReturn(List.of(participant));

        // Act
        List<Expense> result = expenseService.getExpensesByTripId(1);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsSharedExpense());
    }

    @Test
    void getExpenseById_ShouldPopulateSharedExpenseFields() {
        // Arrange
        validExpense.setExpenseId(1);
        ExpenseShare share = new ExpenseShare(1, 100L, new BigDecimal("25.00"));

        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(validExpense));
        when(expenseShareDAL.getSharesByExpenseId(1)).thenReturn(List.of(share));
        when(tripParticipantService.getParticipantsByTripId(1)).thenReturn(List.of(participant));

        // Act
        Optional<Expense> result = expenseService.getExpenseById(1);

        // Assert
        assertTrue(result.isPresent());
        verify(expenseShareDAL, times(1)).getSharesByExpenseId(1);
    }
}
