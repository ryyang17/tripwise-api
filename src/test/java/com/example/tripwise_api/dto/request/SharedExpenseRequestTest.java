package com.example.tripwise_api.dto.request;

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

class SharedExpenseRequestTest {
    private Validator validator;
    private SharedExpenseRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new SharedExpenseRequest();
    }

    @Test
    void canInstantiate() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        request.setDescription("Lunch");
        assertEquals("Lunch", request.getDescription());
    }

    @Test
    void setAndGetAmount_ShouldWorkCorrectly() {
        BigDecimal amount = new BigDecimal("25.00");
        request.setAmount(amount);
        assertEquals(amount, request.getAmount());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        request.setCurrency("USD");
        assertEquals("USD", request.getCurrency());
    }

    @Test
    void setAndGetCategoryId_ShouldWorkCorrectly() {
        request.setCategoryId(3);
        assertEquals(3, request.getCategoryId());
    }

    @Test
    void setAndGetExpenseDate_ShouldWorkCorrectly() {
        LocalDate date = LocalDate.of(2026, 1, 19);
        request.setExpenseDate(date);
        assertEquals(date, request.getExpenseDate());
    }

    @Test
    void setAndGetPaidByParticipantId_ShouldWorkCorrectly() {
        request.setPaidByParticipantId(7);
        assertEquals(7, request.getPaidByParticipantId());
    }

    @Test
    void setAndGetSplitWithParticipantIds_ShouldWorkCorrectly() {
        List<Integer> ids = Arrays.asList(1, 2, 3);
        request.setSplitWithParticipantIds(ids);
        assertEquals(ids, request.getSplitWithParticipantIds());
    }

    @Test
    void setAndGetSplitEvenly_ShouldWorkCorrectly() {
        request.setSplitEvenly(true);
        assertTrue(request.getSplitEvenly());
        request.setSplitEvenly(false);
        assertFalse(request.getSplitEvenly());
    }

    @Test
    void setAndGetCustomSplits_ShouldWorkCorrectly() {
        Map<String, BigDecimal> splits = new HashMap<>();
        splits.put("1", new BigDecimal("10.00"));
        splits.put("2", new BigDecimal("15.00"));
        request.setCustomSplits(splits);
        assertEquals(splits, request.getCustomSplits());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("50.00"));
        request.setCurrency("EUR");
        request.setCategoryId(2);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        request.setSplitEvenly(true);
        request.setCustomSplits(null);
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankDescription_ShouldFailValidation() {
        request.setDescription("");
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("EUR");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description is required")));
    }

    @Test
    void nullAmount_ShouldFailValidation() {
        request.setDescription("Dinner");
        request.setAmount(null);
        request.setCurrency("EUR");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Amount is required")));
    }

    @Test
    void negativeAmount_ShouldFailValidation() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("-5.00"));
        request.setCurrency("EUR");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Amount must be positive")));
    }

    @Test
    void blankCurrency_ShouldFailValidation() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency is required")));
    }

    @Test
    void invalidCurrencyLength_ShouldFailValidation() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("EU");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency must be 3-letter ISO code")));
    }

    @Test
    void nullCategoryId_ShouldFailValidation() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("EUR");
        request.setCategoryId(null);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category ID is required")));
    }

    @Test
    void nullExpenseDate_ShouldFailValidation() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("EUR");
        request.setCategoryId(1);
        request.setExpenseDate(null);
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Expense date is required")));
    }

    @Test
    void nullPaidByParticipantId_ShouldFailValidation() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("EUR");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(null);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Paid by participant ID is required")));
    }

    @Test
    void emptySplitWithParticipantIds_ShouldFailValidation() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("EUR");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(new ArrayList<>());
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Split with participant IDs is required")));
    }

    @Test
    void nullSplitWithParticipantIds_ShouldFailValidation() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("EUR");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(null);
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Split with participant IDs is required")));
    }

    @Test
    void splitEvenlyAndCustomSplits_CanBeNull() {
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("EUR");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.of(2026, 1, 19));
        request.setPaidByParticipantId(1);
        request.setSplitWithParticipantIds(Arrays.asList(1, 2));
        request.setSplitEvenly(null);
        request.setCustomSplits(null);
        Set<ConstraintViolation<SharedExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
