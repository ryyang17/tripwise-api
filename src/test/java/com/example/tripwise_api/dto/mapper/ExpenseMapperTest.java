package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateExpenseRequest;
import com.example.tripwise_api.dto.response.ExpenseResponse;
import com.example.tripwise_api.model.Expense;
import com.example.tripwise_api.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseMapperTest {

    private Expense expense;
    private ExpenseCategory category;
    private CreateExpenseRequest createRequest;

    @BeforeEach
    void setUp() {
        category = new ExpenseCategory();
        category.setCategoryId(1L);
        category.setCategoryName("Food");

        expense = new Expense();
        expense.setExpenseId(1);
        expense.setTripId(1);
        expense.setDescription("Lunch");
        expense.setAmount(new BigDecimal("50.00"));
        expense.setOriginalAmount(new BigDecimal("50.00"));
        expense.setCurrency("USD");
        expense.setCategory(category);
        expense.setExpenseDate(LocalDate.now());
        expense.setCreatedDate(LocalDateTime.now());
        expense.setReceiptUrl("http://example.com/receipt.jpg");
        expense.setValidated(true);
        expense.setPaidById(1);
        expense.setSplitEvenly(true);
        expense.setIsSharedExpense(true);

        createRequest = new CreateExpenseRequest();
        createRequest.setTripId(1);
        createRequest.setDescription("Lunch");
        createRequest.setAmount(new BigDecimal("50.00"));
        createRequest.setCurrency("USD");
        createRequest.setCategoryId(1L);
        createRequest.setExpenseDate(LocalDate.now());
    }

    @Test
    void toResponse_ShouldMapEntityToResponse() {
        // Act
        ExpenseResponse result = ExpenseMapper.toResponse(expense);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getExpenseId().intValue());
        assertEquals(1, result.getTripId().intValue());
        assertEquals("Lunch", result.getDescription());
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertNotNull(result.getCategory());
        assertEquals("Food", result.getCategory().getCategoryName());
        assertTrue(result.getIsValidated());
        assertEquals(1, result.getPaidById());
        assertTrue(result.getSplitEvenly());
    }

    @Test
    void toResponse_WithNullExpense_ShouldReturnNull() {
        // Act
        ExpenseResponse result = ExpenseMapper.toResponse(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toResponse_WithCustomSplits_ShouldConvertSplits() {
        // Arrange
        Map<Integer, Double> customSplits = Map.of(
            1, 25.0,
            2, 25.0
        );
        expense.setCustomSplits(customSplits);

        // Act
        ExpenseResponse result = ExpenseMapper.toResponse(expense);

        // Assert
        assertNotNull(result.getCustomSplits());
        assertEquals(2, result.getCustomSplits().size());
        assertEquals(new BigDecimal("25.0"), result.getCustomSplits().get("1"));
    }

    @Test
    void toResponseList_ShouldMapListOfEntities() {
        // Arrange
        Expense expense2 = new Expense();
        expense2.setExpenseId(2);
        expense2.setDescription("Dinner");
        expense2.setAmount(new BigDecimal("75.00"));

        List<Expense> expenses = List.of(expense, expense2);

        // Act
        List<ExpenseResponse> result = ExpenseMapper.toResponseList(expenses);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getExpenseId().intValue());
        assertEquals(2, result.get(1).getExpenseId().intValue());
    }

    @Test
    void toResponseList_WithEmptyList_ShouldReturnEmptyList() {
        // Act
        List<ExpenseResponse> result = ExpenseMapper.toResponseList(List.of());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

