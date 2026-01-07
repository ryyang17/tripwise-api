package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateBookingRequest;
import com.example.tripwise_api.dto.response.BookingResponse;
import com.example.tripwise_api.model.Booking;
import com.example.tripwise_api.model.BookingType;
import com.example.tripwise_api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private CreateBookingRequest createRequest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        createRequest = new CreateBookingRequest();
        createRequest.setBookingType("FLIGHT");
        createRequest.setProvider("Test Airline");
        createRequest.setBookingReference("REF123");
        createRequest.setPrice(new BigDecimal("500.00"));
        createRequest.setCurrency("USD");
        createRequest.setSearchParams("{\"from\": \"NYC\", \"to\": \"LAX\"}");
        createRequest.setRawPayload("{\"test\": \"data\"}");

        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        booking = new Booking();
        booking.setId(1L);
        booking.setTripId(1L);
        booking.setUser(testUser);
        booking.setBookingType(BookingType.FLIGHT);
        booking.setProvider("Test Airline");
        booking.setBookingReference("REF123");
        booking.setPrice(new BigDecimal("500.00"));
        booking.setCurrency("USD");
        booking.setSearchParams("{\"from\": \"NYC\", \"to\": \"LAX\"}");
        booking.setRawPayload("{\"test\": \"data\"}");
        booking.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void toEntity_ShouldMapRequestToEntity() {
        // Act
        Booking result = BookingMapper.toEntity(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(BookingType.FLIGHT, result.getBookingType());
        assertEquals("Test Airline", result.getProvider());
        assertEquals("REF123", result.getBookingReference());
        assertEquals(new BigDecimal("500.00"), result.getPrice());
        assertEquals("USD", result.getCurrency());
        assertEquals("{\"from\": \"NYC\", \"to\": \"LAX\"}", result.getSearchParams());
        assertEquals("{\"test\": \"data\"}", result.getRawPayload());
    }

    @Test
    void toEntity_WithNullBookingType_ShouldDefaultToOther() {
        // Arrange
        createRequest.setBookingType(null);

        // Act
        Booking result = BookingMapper.toEntity(createRequest);

        // Assert
        assertEquals(BookingType.OTHER, result.getBookingType());
    }

    @Test
    void toEntity_WithInvalidBookingType_ShouldDefaultToOther() {
        // Arrange
        createRequest.setBookingType("INVALID_TYPE");

        // Act
        Booking result = BookingMapper.toEntity(createRequest);

        // Assert
        assertEquals(BookingType.OTHER, result.getBookingType());
    }

    @Test
    void toResponse_ShouldMapEntityToResponse() {
        // Act
        BookingResponse result = BookingMapper.toResponse(booking);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getTripId());
        assertEquals("FLIGHT", result.getBookingType());
        assertEquals("Test Airline", result.getProvider());
        assertEquals("REF123", result.getBookingReference());
        assertEquals(new BigDecimal("500.00"), result.getPrice());
        assertEquals("USD", result.getCurrency());
        assertEquals("{\"from\": \"NYC\", \"to\": \"LAX\"}", result.getSearchParams());
        assertEquals("{\"test\": \"data\"}", result.getRawPayload());
        assertNotNull(result.getCreatedDate());
    }

    @Test
    void toResponse_WithNullBookingType_ShouldReturnNull() {
        // Arrange
        booking.setBookingType(null);

        // Act
        BookingResponse result = BookingMapper.toResponse(booking);

        // Assert
        assertNull(result.getBookingType());
    }

    @Test
    void toResponseList_ShouldMapListOfEntities() {
        // Arrange
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setTripId(1L);
        booking2.setBookingType(BookingType.HOTEL);
        booking2.setProvider("Test Hotel");

        List<Booking> bookings = List.of(booking, booking2);

        // Act
        List<BookingResponse> result = BookingMapper.toResponseList(bookings);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals("FLIGHT", result.get(0).getBookingType());
        assertEquals("HOTEL", result.get(1).getBookingType());
    }

    @Test
    void toResponseList_WithEmptyList_ShouldReturnEmptyList() {
        // Act
        List<BookingResponse> result = BookingMapper.toResponseList(List.of());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

