package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateBudgetRequestTest {

    private Validator validator;
    private CreateBudgetRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new CreateBudgetRequest();
    }

    @Test
    void canInstantiate() {
        CreateBudgetRequest req = new CreateBudgetRequest();
        assertNotNull(req);
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        // Act
        CreateBudgetRequest req = new CreateBudgetRequest(1000.0, "EUR");

        // Assert
        assertEquals(1000.0, req.getTotalBudget());
        assertEquals("EUR", req.getCurrency());
    }

    @Test
    void setAndGetTotalBudget_ShouldWorkCorrectly() {
        // Act
        request.setTotalBudget(500.0);

        // Assert
        assertEquals(500.0, request.getTotalBudget());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        // Act
        request.setCurrency("USD");

        // Assert
        assertEquals("USD", request.getCurrency());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setTotalBudget(1500.0);
        request.setCurrency("EUR");

        // Act
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullTotalBudget_ShouldFailValidation() {
        // Arrange
        request.setTotalBudget(null);
        request.setCurrency("EUR");

        // Act
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Total budget is required")));
    }

    @Test
    void negativeTotalBudget_ShouldFailValidation() {
        // Arrange
        request.setTotalBudget(-100.0);
        request.setCurrency("EUR");

        // Act
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Total budget must be positive")));
    }

    @Test
    void zeroTotalBudget_ShouldFailValidation() {
        // Arrange
        request.setTotalBudget(0.0);
        request.setCurrency("EUR");

        // Act
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Total budget must be positive")));
    }

    @Test
    void blankCurrency_ShouldFailValidation() {
        // Arrange
        request.setTotalBudget(1000.0);
        request.setCurrency("");

        // Act
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency must be 3-letter ISO code")));
    }

    @Test
    void nullCurrency_ShouldFailValidation() {
        // Arrange
        request.setTotalBudget(1000.0);
        request.setCurrency(null);

        // Act
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency is required")));
    }

    @Test
    void invalidCurrencyLength_ShouldFailValidation() {
        // Arrange
        request.setTotalBudget(1000.0);
        request.setCurrency("EURO");

        // Act
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Currency must be 3-letter ISO code")));
    }

    @Test
    void validCurrencyCodes_ShouldPassValidation() {
        // Test various valid currency codes
        String[] validCurrencies = {"USD", "EUR", "GBP", "JPY", "CAD"};

        for (String currency : validCurrencies) {
            // Arrange
            request.setTotalBudget(1000.0);
            request.setCurrency(currency);

            // Act
            Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

            // Assert
            assertTrue(violations.isEmpty(), "Currency " + currency + " should be valid");
        }
    }
}

