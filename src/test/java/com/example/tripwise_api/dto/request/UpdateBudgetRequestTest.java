package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateBudgetRequestTest {
    private Validator validator;
    private UpdateBudgetRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new UpdateBudgetRequest();
    }

    @Test
    void canInstantiate() {
        UpdateBudgetRequest req = new UpdateBudgetRequest();
        assertNotNull(req);
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        UpdateBudgetRequest req = new UpdateBudgetRequest(2500.0, "USD");
        assertEquals(2500.0, req.getTotalBudget());
        assertEquals("USD", req.getCurrency());
    }

    @Test
    void setAndGetTotalBudget_ShouldWorkCorrectly() {
        request.setTotalBudget(1800.0);
        assertEquals(1800.0, request.getTotalBudget());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        request.setCurrency("GBP");
        assertEquals("GBP", request.getCurrency());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        request.setTotalBudget(3000.0);
        request.setCurrency("EUR");
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullTotalBudget_ShouldFailValidation() {
        request.setTotalBudget(null);
        request.setCurrency("EUR");
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Total budget is required")));
    }

    @Test
    void negativeTotalBudget_ShouldFailValidation() {
        request.setTotalBudget(-500.0);
        request.setCurrency("EUR");
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Total budget must be positive")));
    }

    @Test
    void zeroTotalBudget_ShouldFailValidation() {
        request.setTotalBudget(0.0);
        request.setCurrency("EUR");
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Total budget must be positive")));
    }

    @Test
    void blankCurrency_ShouldFailValidation() {
        request.setTotalBudget(2000.0);
        request.setCurrency("");
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency must be 3-letter ISO code")));
    }

    @Test
    void nullCurrency_ShouldFailValidation() {
        request.setTotalBudget(2000.0);
        request.setCurrency(null);
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency is required")));
    }

    @Test
    void invalidCurrencyLength_ShouldFailValidation() {
        request.setTotalBudget(2000.0);
        request.setCurrency("DOLLAR");
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency must be 3-letter ISO code")));
    }

    @Test
    void tooShortCurrency_ShouldFailValidation() {
        request.setTotalBudget(2000.0);
        request.setCurrency("US");
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency must be 3-letter ISO code")));
    }

    @Test
    void validCurrencyCodes_ShouldPassValidation() {
        String[] validCurrencies = {"USD", "EUR", "GBP", "JPY", "CAD", "CHF", "AUD"};
        for (String currency : validCurrencies) {
            request.setTotalBudget(2000.0);
            request.setCurrency(currency);
            Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty(), "Currency " + currency + " should be valid");
        }
    }

    @Test
    void largeAmounts_ShouldPassValidation() {
        request.setTotalBudget(999999.99);
        request.setCurrency("EUR");
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void smallAmounts_ShouldPassValidation() {
        request.setTotalBudget(0.01);
        request.setCurrency("EUR");
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
