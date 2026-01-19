package com.example.tripwise_api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateBookingRequestTest {

    private Validator validator;
    private CreateBookingRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new CreateBookingRequest();
    }

    @Test
    void canInstantiate() {
        CreateBookingRequest req = new CreateBookingRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetBookingType_ShouldWorkCorrectly() {
        // Act
        request.setBookingType("FLIGHT");

        // Assert
        assertEquals("FLIGHT", request.getBookingType());
    }

    @Test
    void setAndGetBookingReference_ShouldWorkCorrectly() {
        // Act
        request.setBookingReference("REF123456");

        // Assert
        assertEquals("REF123456", request.getBookingReference());
    }

    @Test
    void setAndGetPrice_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal price = new BigDecimal("299.99");

        // Act
        request.setPrice(price);

        // Assert
        assertEquals(price, request.getPrice());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        // Act
        request.setCurrency("EUR");

        // Assert
        assertEquals("EUR", request.getCurrency());
    }

    @Test
    void setAndGetSearchParams_ShouldWorkCorrectly() {
        // Act
        request.setSearchParams("departure=AMS&arrival=BCN");

        // Assert
        assertEquals("departure=AMS&arrival=BCN", request.getSearchParams());
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setBookingType("HOTEL");

        // Act
        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }


    @Test
    void flightBookingType_ShouldBeValid() {
        // Arrange
        request.setBookingType("FLIGHT");
        request.setBookingReference("FL123");
        request.setPrice(new BigDecimal("450.00"));
        request.setCurrency("USD");

        // Act
        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals("FLIGHT", request.getBookingType());
    }

    @Test
    void hotelBookingType_ShouldBeValid() {
        // Arrange
        request.setBookingType("HOTEL");
        request.setBookingReference("HTL456");
        request.setPrice(new BigDecimal("150.00"));
        request.setCurrency("EUR");

        // Act
        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals("HOTEL", request.getBookingType());
    }
}

