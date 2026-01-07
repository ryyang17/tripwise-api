package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Booking booking;
    private User testUser;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void constructor_ShouldInitializeCreatedDate() {
        // Act
        Booking newBooking = new Booking();

        // Assert
        assertNotNull(newBooking.getCreatedDate());
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        // Act
        booking.setId(1L);

        // Assert
        assertEquals(1L, booking.getId());
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        // Act
        booking.setTripId(1L);

        // Assert
        assertEquals(1L, booking.getTripId());
    }

    @Test
    void setAndGetUser_ShouldWorkCorrectly() {
        // Act
        booking.setUser(testUser);

        // Assert
        assertEquals(testUser, booking.getUser());
        assertEquals(1L, booking.getUser().getId());
    }

    @Test
    void setAndGetBookingType_ShouldWorkCorrectly() {
        // Act
        booking.setBookingType(BookingType.FLIGHT);

        // Assert
        assertEquals(BookingType.FLIGHT, booking.getBookingType());
    }

    @Test
    void setAndGetProvider_ShouldWorkCorrectly() {
        // Act
        booking.setProvider("Test Airline");

        // Assert
        assertEquals("Test Airline", booking.getProvider());
    }

    @Test
    void setAndGetBookingReference_ShouldWorkCorrectly() {
        // Act
        booking.setBookingReference("REF123");

        // Assert
        assertEquals("REF123", booking.getBookingReference());
    }

    @Test
    void setAndGetPrice_ShouldWorkCorrectly() {
        // Arrange
        BigDecimal price = new BigDecimal("500.00");

        // Act
        booking.setPrice(price);

        // Assert
        assertEquals(price, booking.getPrice());
    }

    @Test
    void setAndGetCurrency_ShouldWorkCorrectly() {
        // Act
        booking.setCurrency("USD");

        // Assert
        assertEquals("USD", booking.getCurrency());
    }

    @Test
    void setAndGetSearchParams_ShouldWorkCorrectly() {
        // Act
        booking.setSearchParams("{\"from\": \"NYC\", \"to\": \"LAX\"}");

        // Assert
        assertEquals("{\"from\": \"NYC\", \"to\": \"LAX\"}", booking.getSearchParams());
    }

    @Test
    void setAndGetRawPayload_ShouldWorkCorrectly() {
        // Act
        booking.setRawPayload("{\"test\": \"data\"}");

        // Assert
        assertEquals("{\"test\": \"data\"}", booking.getRawPayload());
    }

    @Test
    void setAndGetCreatedDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        booking.setCreatedDate(now);

        // Assert
        assertEquals(now, booking.getCreatedDate());
    }

    @Test
    void bookingType_AllEnumValues_ShouldBeAccessible() {
        // Assert
        assertNotNull(BookingType.FLIGHT);
        assertNotNull(BookingType.HOTEL);
        assertNotNull(BookingType.OTHER);
    }

    @Test
    void bookingType_ValueOf_ShouldWorkCorrectly() {
        // Act
        BookingType type = BookingType.valueOf("FLIGHT");

        // Assert
        assertEquals(BookingType.FLIGHT, type);
    }

    @Test
    void bookingType_ShouldHaveThreeValues() {
        // Act & Assert
        assertEquals(3, BookingType.values().length);
    }
}

