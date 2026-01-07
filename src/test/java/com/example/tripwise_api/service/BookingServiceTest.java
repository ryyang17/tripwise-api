package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.BookingDAL;
import com.example.tripwise_api.dto.request.CreateBookingRequest;
import com.example.tripwise_api.model.Booking;
import com.example.tripwise_api.model.BookingType;
import com.example.tripwise_api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingDAL bookingDAL;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Booking testBooking;
    private Long testTripId;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password");
        testUser.setId(1L);

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setUser(testUser);
        testBooking.setBookingType(BookingType.FLIGHT);
        testBooking.setProvider("Amadeus");
        testBooking.setBookingReference("ABC123");
        testBooking.setPrice(new BigDecimal("299.99"));
        testBooking.setCurrency("EUR");
        testBooking.setSearchParams("{\"origin\":\"AMS\",\"destination\":\"BCN\"}");
        testBooking.setRawPayload("{\"data\":{\"flight\":\"details\"}}");
        testBooking.setCreatedDate(LocalDateTime.now());
        testTripId = 1L;
    }

    @Test
    void testCreateBookingForUser() {
        // Arrange
        when(bookingDAL.saveBooking(any(Booking.class))).thenReturn(testBooking);

        // Act
        Booking result = bookingService.createBookingForUser(testUser, testBooking, testTripId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(BookingType.FLIGHT, result.getBookingType());
        assertEquals("Amadeus", result.getProvider());
        verify(bookingDAL, times(1)).saveBooking(any(Booking.class));
    }

    @Test
    void testGetBookingsForUser() {
        // Arrange
        List<Booking> bookings = Collections.singletonList(testBooking);
        when(bookingDAL.getBookingsByUserId(1L)).thenReturn(bookings);

        // Act
        List<Booking> result = bookingService.getBookingsForUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBooking, result.get(0));
        verify(bookingDAL, times(1)).getBookingsByUserId(1L);
    }

    @Test
    void testGetBookingById() {
        // Arrange
        when(bookingDAL.getBookingById(1L)).thenReturn(Optional.of(testBooking));

        // Act
        Optional<Booking> result = bookingService.getBookingById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBooking, result.get());
        verify(bookingDAL, times(1)).getBookingById(1L);
    }

    @Test
    void testGetBookingById_NotFound() {
        // Arrange
        when(bookingDAL.getBookingById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Booking> result = bookingService.getBookingById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(bookingDAL, times(1)).getBookingById(999L);
    }

    @Test
    void testDeleteBookingDelegatesToDal() {
        // Act
        bookingService.deleteBooking(5L);

        // Assert
        verify(bookingDAL, times(1)).deleteBooking(5L);
        verifyNoMoreInteractions(bookingDAL);
    }

    @Test
    void testUpdateBookingUpdatesFieldsAndSaves() {
        // Arrange
        Booking existing = new Booking();
        existing.setId(2L);
        existing.setBookingType(BookingType.FLIGHT);
        existing.setProvider("OldProvider");
        existing.setBookingReference("OLD123");
        existing.setPrice(new BigDecimal("100.00"));
        existing.setCurrency("USD");
        existing.setSearchParams("{}");
        existing.setRawPayload("oldPayload");

        CreateBookingRequest request = new CreateBookingRequest();
        request.setBookingType("hotel");
        request.setProvider("NewProvider");
        request.setBookingReference("NEW456");
        request.setPrice(new BigDecimal("200.50"));
        request.setCurrency("EUR");
        request.setSearchParams("{\"city\":\"BCN\"}");
        request.setRawPayload("newPayload");

        when(bookingDAL.getBookingById(2L)).thenReturn(Optional.of(existing));
        when(bookingDAL.saveBooking(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = bookingService.updateBooking(2L, request);

        // Assert
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingDAL).getBookingById(2L);
        verify(bookingDAL).saveBooking(captor.capture());

        Booking saved = captor.getValue();
        assertSame(existing, result);
        assertSame(existing, saved);
        assertEquals(BookingType.HOTEL, saved.getBookingType());
        assertEquals("NewProvider", saved.getProvider());
        assertEquals("NEW456", saved.getBookingReference());
        assertEquals(new BigDecimal("200.50"), saved.getPrice());
        assertEquals("EUR", saved.getCurrency());
        assertEquals("{\"city\":\"BCN\"}", saved.getSearchParams());
        assertEquals("newPayload", saved.getRawPayload());
        verifyNoMoreInteractions(bookingDAL);
    }

    @Test
    void testUpdateBookingThrowsWhenNotFound() {
        // Arrange
        when(bookingDAL.getBookingById(3L)).thenReturn(Optional.empty());

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateBooking(3L, new CreateBookingRequest()));
        assertTrue(ex.getMessage().contains("3"));
        verify(bookingDAL).getBookingById(3L);
        verifyNoMoreInteractions(bookingDAL);
    }
}
