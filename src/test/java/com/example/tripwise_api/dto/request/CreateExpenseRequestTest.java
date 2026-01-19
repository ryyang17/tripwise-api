package com.example.tripwise_api.dto.request;

import com.example.tripwise_api.model.ExpenseCategory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CreateExpenseRequestTest {

    private Validator validator;
    private CreateExpenseRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new CreateExpenseRequest();
    }

    @Test
    void canInstantiate() {
        CreateExpenseRequest req = new CreateExpenseRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        // Act
        request.setTripId(1);

        // Assert
        assertEquals(1, request.getTripId());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Act
        request.setDescription("Restaurant bill");

        // Assert
        assertEquals("Restaurant bill", request.getDescription());
    }

    @Test
    void setAndGetAmount_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal amount = new BigDecimal("45.50");

        // Act
        request.setAmount(amount);

        // Assert
        assertEquals(amount, request.getAmount());
    }

    @Test
    void setAndGetOriginalAmount_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal originalAmount = new BigDecimal("50.00");

        // Act
        request.setOriginalAmount(originalAmount);

        // Assert
        assertEquals(originalAmount, request.getOriginalAmount());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        // Act
        request.setCurrency("USD");

        // Assert
        assertEquals("USD", request.getCurrency());
    }

    @Test
    void setAndGetCategory_ShouldWorkCorrectly() {
        // Arrange
        ExpenseCategory category = new ExpenseCategory();
        category.setCategoryName("Food");

        // Act
        request.setCategory(category);

        // Assert
        assertEquals(category, request.getCategory());
    }

    @Test
    void setAndGetCategoryId_ShouldWorkCorrectly() {
        // Act
        request.setCategoryId(1L);

        // Assert
        assertEquals(1L, request.getCategoryId());
    }

    @Test
    void setAndGetExpenseDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 1, 15);

        // Act
        request.setExpenseDate(date);

        // Assert
        assertEquals(date, request.getExpenseDate());
    }

    @Test
    void setAndGetReceiptUrl_ShouldWorkCorrectly() {
        // Act
        request.setReceiptUrl("https://example.com/receipt.jpg");

        // Assert
        assertEquals("https://example.com/receipt.jpg", request.getReceiptUrl());
    }

    @Test
    void setAndGetIsValidated_ShouldWorkCorrectly() {
        // Act
        request.setIsValidated(true);

        // Assert
        assertTrue(request.getIsValidated());
    }

    @Test
    void defaultIsValidated_ShouldBeFalse() {
        // Assert
        assertFalse(request.getIsValidated());
    }

    @Test
    void setAndGetPaidById_ShouldWorkCorrectly() {
        // Act
        request.setPaidById(2);

        // Assert
        assertEquals(2, request.getPaidById());
    }

    @Test
    void setAndGetSplitWith_ShouldWorkCorrectly() {
        // Arrange
        List<Integer> splitWith = Arrays.asList(1, 2, 3);

        // Act
        request.setSplitWith(splitWith);

        // Assert
        assertEquals(splitWith, request.getSplitWith());
    }

    @Test
    void setAndGetSplitEvenly_ShouldWorkCorrectly() {
        // Act
        request.setSplitEvenly(true);

        // Assert
        assertTrue(request.getSplitEvenly());
    }

    @Test
    void defaultSplitEvenly_ShouldBeFalse() {
        // Assert
        assertFalse(request.getSplitEvenly());
    }

    @Test
    void setAndGetIsSharedExpense_ShouldWorkCorrectly() {
        // Act
        request.setIsSharedExpense(true);

        // Assert
        assertTrue(request.getIsSharedExpense());
    }

    @Test
    void defaultIsSharedExpense_ShouldBeFalse() {
        // Assert
        assertFalse(request.getIsSharedExpense());
    }

    @Test
    void setAndGetCustomSplits_ShouldWorkCorrectly() {
        // Arrange
        Map<String, BigDecimal> customSplits = new HashMap<>();
        customSplits.put("1", new BigDecimal("20.00"));
        customSplits.put("2", new BigDecimal("25.50"));

        // Act
        request.setCustomSplits(customSplits);

        // Assert
        assertEquals(customSplits, request.getCustomSplits());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setTripId(1);
        request.setDescription("Lunch");
        request.setAmount(new BigDecimal("30.00"));
        request.setCurrency("EUR");
        request.setExpenseDate(LocalDate.of(2026, 1, 19));

        // Act
        Set<ConstraintViolation<CreateExpenseRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullTripId_ShouldFailValidation() {
        // Arrange
        request.setTripId(null);
        request.setDescription("Lunch");
        request.setAmount(new BigDecimal("30.00"));
        request.setCurrency("EUR");
        request.setExpenseDate(LocalDate.of(2026, 1, 19));

        // Act
        Set<ConstraintViolation<CreateExpenseRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Trip ID is required")));
    }

    @Test
    void blankDescription_ShouldFailValidation() {
        // Arrange
        request.setTripId(1);
        request.setDescription("");
        request.setAmount(new BigDecimal("30.00"));
        request.setCurrency("EUR");
        request.setExpenseDate(LocalDate.of(2026, 1, 19));

        // Act
        Set<ConstraintViolation<CreateExpenseRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description is required")));
    }

    @Test
    void nullAmount_ShouldFailValidation() {
        // Arrange
        request.setTripId(1);
        request.setDescription("Lunch");
        request.setAmount(null);
        request.setCurrency("EUR");
        request.setExpenseDate(LocalDate.of(2026, 1, 19));

        // Act
        Set<ConstraintViolation<CreateExpenseRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Amount is required")));
    }

    @Test
    void negativeAmount_ShouldFailValidation() {
        // Arrange
        request.setTripId(1);
        request.setDescription("Lunch");
        request.setAmount(new BigDecimal("-30.00"));
        request.setCurrency("EUR");
        request.setExpenseDate(LocalDate.of(2026, 1, 19));

        // Act
        Set<ConstraintViolation<CreateExpenseRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Amount must be positive")));
    }

    @Test
    void invalidCurrency_ShouldFailValidation() {
        // Arrange
        request.setTripId(1);
        request.setDescription("Lunch");
        request.setAmount(new BigDecimal("30.00"));
        request.setCurrency("EURO");
        request.setExpenseDate(LocalDate.of(2026, 1, 19));

        // Act
        Set<ConstraintViolation<CreateExpenseRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency must be 3-letter ISO code")));
    }

    @Test
    void nullExpenseDate_ShouldFailValidation() {
        // Arrange
        request.setTripId(1);
        request.setDescription("Lunch");
        request.setAmount(new BigDecimal("30.00"));
        request.setCurrency("EUR");
        request.setExpenseDate(null);

        // Act
        Set<ConstraintViolation<CreateExpenseRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Expense date is required")));
    }
}

