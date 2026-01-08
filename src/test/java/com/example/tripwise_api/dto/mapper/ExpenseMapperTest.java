package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateExpenseRequest;
import com.example.tripwise_api.dto.request.UpdateExpenseRequest;
import com.example.tripwise_api.dto.response.ExpenseResponse;
import com.example.tripwise_api.model.Expense;
import com.example.tripwise_api.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    @Test
    void toResponseList_WithNullList_ShouldReturnNull() {
        // Act
        List<ExpenseResponse> result = ExpenseMapper.toResponseList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_ShouldMapRequestToEntity() {
        // Act
        Expense result = ExpenseMapper.toEntity(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTripId());
        assertEquals("Lunch", result.getDescription());
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        assertEquals(new BigDecimal("50.00"), result.getOriginalAmount());
        assertEquals("USD", result.getCurrency());
        assertNotNull(result.getExpenseDate());
        assertFalse(result.isValidated()); // Default false
    }

    @Test
    void toEntity_WithNullRequest_ShouldReturnNull() {
        // Act
        Expense result = ExpenseMapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_WithOriginalAmount_ShouldUseOriginalAmount() {
        // Arrange
        createRequest.setOriginalAmount(new BigDecimal("60.00"));

        // Act
        Expense result = ExpenseMapper.toEntity(createRequest);

        // Assert
        assertEquals(new BigDecimal("60.00"), result.getOriginalAmount());
    }

    @Test
    void toEntity_WithoutOriginalAmount_ShouldUseAmount() {
        // Arrange
        createRequest.setOriginalAmount(null);

        // Act
        Expense result = ExpenseMapper.toEntity(createRequest);

        // Assert
        assertEquals(new BigDecimal("50.00"), result.getOriginalAmount());
    }

    @Test
    void toEntity_WithSharedExpenseFields_ShouldMapCorrectly() {
        // Arrange
        createRequest.setPaidById(1);
        createRequest.setSplitWith(List.of(1, 2, 3));
        createRequest.setSplitEvenly(true);
        createRequest.setIsSharedExpense(true);

        // Act
        Expense result = ExpenseMapper.toEntity(createRequest);

        // Assert
        assertEquals(1, result.getPaidById());
        assertEquals(List.of(1, 2, 3), result.getSplitWith());
        assertTrue(result.getSplitEvenly());
        assertTrue(result.getIsSharedExpense());
    }

    @Test
    void toEntity_WithCustomSplits_ShouldConvertTypes() {
        // Arrange
        Map<String, BigDecimal> customSplits = new HashMap<>();
        customSplits.put("1", new BigDecimal("25.00"));
        customSplits.put("2", new BigDecimal("25.00"));
        createRequest.setCustomSplits(customSplits);

        // Act
        Expense result = ExpenseMapper.toEntity(createRequest);

        // Assert
        assertNotNull(result.getCustomSplits());
        assertEquals(2, result.getCustomSplits().size());
        assertEquals(25.0, result.getCustomSplits().get(1));
        assertEquals(25.0, result.getCustomSplits().get(2));
    }

    @Test
    void toEntity_WithValidatedTrue_ShouldSetValidated() {
        // Arrange
        createRequest.setIsValidated(true);

        // Act
        Expense result = ExpenseMapper.toEntity(createRequest);

        // Assert
        assertTrue(result.isValidated());
    }

    @Test
    void extractCategoryId_FromCreateRequest_WithCategoryObject_ShouldReturnCategoryId() {
        // Arrange
        ExpenseCategory cat = new ExpenseCategory();
        cat.setCategoryId(5L);
        createRequest.setCategory(cat);

        // Act
        Long result = ExpenseMapper.extractCategoryId(createRequest);

        // Assert
        assertEquals(5L, result);
    }

    @Test
    void extractCategoryId_FromCreateRequest_WithCategoryIdField_ShouldReturnCategoryId() {
        // Arrange
        createRequest.setCategory(null);
        createRequest.setCategoryId(10L);

        // Act
        Long result = ExpenseMapper.extractCategoryId(createRequest);

        // Assert
        assertEquals(10L, result);
    }

    @Test
    void extractCategoryId_FromCreateRequest_WithNull_ShouldReturnNull() {
        // Act
        Long result = ExpenseMapper.extractCategoryId((CreateExpenseRequest) null);

        // Assert
        assertNull(result);
    }

    @Test
    void extractCategoryId_FromUpdateRequest_WithCategoryObject_ShouldReturnCategoryId() {
        // Arrange
        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest();
        ExpenseCategory cat = new ExpenseCategory();
        cat.setCategoryId(7L);
        updateRequest.setCategory(cat);

        // Act
        Long result = ExpenseMapper.extractCategoryId(updateRequest);

        // Assert
        assertEquals(7L, result);
    }

    @Test
    void extractCategoryId_FromUpdateRequest_WithCategoryIdField_ShouldReturnCategoryId() {
        // Arrange
        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest();
        updateRequest.setCategoryId(12L);

        // Act
        Long result = ExpenseMapper.extractCategoryId(updateRequest);

        // Assert
        assertEquals(12L, result);
    }

    @Test
    void extractCategoryId_FromUpdateRequest_WithNull_ShouldReturnNull() {
        // Act
        Long result = ExpenseMapper.extractCategoryId((UpdateExpenseRequest) null);

        // Assert
        assertNull(result);
    }

    @Test
    void updateEntity_ShouldUpdateAllFields() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setExpenseId(1);
        existingExpense.setDescription("Old Description");

        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest();
        updateRequest.setExpenseId(1);
        updateRequest.setTripId(2);
        updateRequest.setDescription("New Description");
        updateRequest.setAmount(new BigDecimal("100.00"));
        updateRequest.setOriginalAmount(new BigDecimal("110.00"));
        updateRequest.setCurrency("EUR");
        updateRequest.setExpenseDate(LocalDate.now().plusDays(1));
        updateRequest.setReceiptUrl("http://example.com/new-receipt.jpg");
        updateRequest.setIsValidated(true);
        updateRequest.setPaidById(2);
        updateRequest.setSplitWith(List.of(2, 3));
        updateRequest.setSplitEvenly(true);
        updateRequest.setIsSharedExpense(true);

        Map<String, BigDecimal> customSplits = new HashMap<>();
        customSplits.put("2", new BigDecimal("50.00"));
        customSplits.put("3", new BigDecimal("50.00"));
        updateRequest.setCustomSplits(customSplits);

        // Act
        ExpenseMapper.updateEntity(existingExpense, updateRequest);

        // Assert
        assertEquals(2, existingExpense.getTripId());
        assertEquals("New Description", existingExpense.getDescription());
        assertEquals(new BigDecimal("100.00"), existingExpense.getAmount());
        assertEquals(new BigDecimal("110.00"), existingExpense.getOriginalAmount());
        assertEquals("EUR", existingExpense.getCurrency());
        assertTrue(existingExpense.isValidated());
        assertEquals(2, existingExpense.getPaidById());
        assertTrue(existingExpense.getSplitEvenly());
        assertTrue(existingExpense.getIsSharedExpense());
        assertNotNull(existingExpense.getCustomSplits());
        assertEquals(50.0, existingExpense.getCustomSplits().get(2));
    }

    @Test
    void updateEntity_WithNullExpense_ShouldNotThrow() {
        // Arrange
        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest();
        updateRequest.setDescription("Test");

        // Act & Assert
        assertDoesNotThrow(() -> ExpenseMapper.updateEntity(null, updateRequest));
    }

    @Test
    void updateEntity_WithNullRequest_ShouldNotThrow() {
        // Arrange
        Expense existingExpense = new Expense();

        // Act & Assert
        assertDoesNotThrow(() -> ExpenseMapper.updateEntity(existingExpense, null));
    }

    @Test
    void updateEntity_WithPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setDescription("Original");
        existingExpense.setAmount(new BigDecimal("50.00"));

        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest();
        updateRequest.setDescription("Updated");
        // Amount is null, should not be updated

        // Act
        ExpenseMapper.updateEntity(existingExpense, updateRequest);

        // Assert
        assertEquals("Updated", existingExpense.getDescription());
        assertEquals(new BigDecimal("50.00"), existingExpense.getAmount());
    }

    @Test
    void toResponse_WithNullCustomSplits_ShouldNotSetCustomSplits() {
        // Arrange
        expense.setCustomSplits(null);

        // Act
        ExpenseResponse result = ExpenseMapper.toResponse(expense);

        // Assert
        assertNotNull(result);
        assertNull(result.getCustomSplits());
    }

    @Test
    void toResponse_WithEmptyCustomSplits_ShouldConvertCorrectly() {
        // Arrange
        expense.setCustomSplits(new HashMap<>());

        // Act
        ExpenseResponse result = ExpenseMapper.toResponse(expense);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCustomSplits());
        assertTrue(result.getCustomSplits().isEmpty());
    }

    @Test
    void toEntity_WithNullSharedExpenseFields_ShouldUseDefaults() {
        // Arrange
        createRequest.setSplitEvenly(null);
        createRequest.setIsSharedExpense(null);
        createRequest.setIsValidated(null);

        // Act
        Expense result = ExpenseMapper.toEntity(createRequest);

        // Assert
        assertFalse(result.getSplitEvenly());
        assertFalse(result.getIsSharedExpense());
        assertFalse(result.isValidated());
    }

    @Test
    void toEntity_WithNullCustomSplits_ShouldNotSetCustomSplits() {
        // Arrange
        createRequest.setCustomSplits(null);

        // Act
        Expense result = ExpenseMapper.toEntity(createRequest);

        // Assert
        assertNull(result.getCustomSplits());
    }
}

