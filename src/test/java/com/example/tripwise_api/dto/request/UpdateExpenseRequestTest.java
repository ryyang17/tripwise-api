package com.example.tripwise_api.dto.request;

import com.example.tripwise_api.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UpdateExpenseRequestTest {
    private UpdateExpenseRequest request;

    @BeforeEach
    void setUp() {
        request = new UpdateExpenseRequest();
    }

    @Test
    void canInstantiate() {
        UpdateExpenseRequest req = new UpdateExpenseRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetExpenseId_ShouldWorkCorrectly() {
        request.setExpenseId(1);
        assertEquals(1, request.getExpenseId());
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        request.setTripId(2);
        assertEquals(2, request.getTripId());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        request.setDescription("Dinner");
        assertEquals("Dinner", request.getDescription());
    }

    @Test
    void setAndGetAmount_ShouldWorkCorrectly() {
        BigDecimal amount = new BigDecimal("45.50");
        request.setAmount(amount);
        assertEquals(amount, request.getAmount());
    }

    @Test
    void setAndGetOriginalAmount_ShouldWorkCorrectly() {
        BigDecimal originalAmount = new BigDecimal("50.00");
        request.setOriginalAmount(originalAmount);
        assertEquals(originalAmount, request.getOriginalAmount());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        request.setCurrency("USD");
        assertEquals("USD", request.getCurrency());
    }

    @Test
    void setAndGetCategory_ShouldWorkCorrectly() {
        ExpenseCategory category = new ExpenseCategory();
        category.setCategoryName("Food");
        request.setCategory(category);
        assertEquals(category, request.getCategory());
    }

    @Test
    void setAndGetCategoryId_ShouldWorkCorrectly() {
        request.setCategoryId(10L);
        assertEquals(10L, request.getCategoryId());
    }

    @Test
    void setAndGetExpenseDate_ShouldWorkCorrectly() {
        LocalDate date = LocalDate.of(2026, 1, 19);
        request.setExpenseDate(date);
        assertEquals(date, request.getExpenseDate());
    }

    @Test
    void setAndGetReceiptUrl_ShouldWorkCorrectly() {
        request.setReceiptUrl("https://example.com/receipt.jpg");
        assertEquals("https://example.com/receipt.jpg", request.getReceiptUrl());
    }

    @Test
    void setAndGetIsValidated_ShouldWorkCorrectly() {
        request.setIsValidated(true);
        assertTrue(request.getIsValidated());
        request.setIsValidated(false);
        assertFalse(request.getIsValidated());
    }

    @Test
    void setAndGetPaidById_ShouldWorkCorrectly() {
        request.setPaidById(5);
        assertEquals(5, request.getPaidById());
    }

    @Test
    void setAndGetSplitWith_ShouldWorkCorrectly() {
        List<Integer> splitWith = Arrays.asList(1, 2, 3);
        request.setSplitWith(splitWith);
        assertEquals(splitWith, request.getSplitWith());
    }

    @Test
    void setAndGetSplitEvenly_ShouldWorkCorrectly() {
        request.setSplitEvenly(true);
        assertTrue(request.getSplitEvenly());
        request.setSplitEvenly(false);
        assertFalse(request.getSplitEvenly());
    }

    @Test
    void setAndGetIsSharedExpense_ShouldWorkCorrectly() {
        request.setIsSharedExpense(true);
        assertTrue(request.getIsSharedExpense());
        request.setIsSharedExpense(false);
        assertFalse(request.getIsSharedExpense());
    }

    @Test
    void setAndGetCustomSplits_ShouldWorkCorrectly() {
        Map<String, BigDecimal> customSplits = new HashMap<>();
        customSplits.put("1", new BigDecimal("20.00"));
        customSplits.put("2", new BigDecimal("25.50"));
        request.setCustomSplits(customSplits);
        assertEquals(customSplits, request.getCustomSplits());
    }

    @Test
    void nullFields_ShouldBeAllowed() {
        request.setDescription(null);
        request.setAmount(null);
        request.setOriginalAmount(null);
        request.setCurrency(null);
        request.setCategory(null);
        request.setCategoryId(null);
        request.setExpenseDate(null);
        request.setReceiptUrl(null);
        request.setIsValidated(null);
        request.setPaidById(null);
        request.setSplitWith(null);
        request.setSplitEvenly(null);
        request.setIsSharedExpense(null);
        request.setCustomSplits(null);
        assertNull(request.getDescription());
        assertNull(request.getAmount());
        assertNull(request.getOriginalAmount());
        assertNull(request.getCurrency());
        assertNull(request.getCategory());
        assertNull(request.getCategoryId());
        assertNull(request.getExpenseDate());
        assertNull(request.getReceiptUrl());
        assertNull(request.getIsValidated());
        assertNull(request.getPaidById());
        assertNull(request.getSplitWith());
        assertNull(request.getSplitEvenly());
        assertNull(request.getIsSharedExpense());
        assertNull(request.getCustomSplits());
    }
}
