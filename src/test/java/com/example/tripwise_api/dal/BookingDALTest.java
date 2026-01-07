package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.Booking;
import com.example.tripwise_api.model.BookingType;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingDALTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingDAL bookingDAL;

    private Booking testBooking;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setTripId(1L);
        testBooking.setUser(testUser);
        testBooking.setBookingType(BookingType.FLIGHT);
        testBooking.setProvider("Test Airline");
        testBooking.setBookingReference("REF123");
        testBooking.setPrice(new BigDecimal("500.00"));
        testBooking.setCurrency("USD");
        testBooking.setRawPayload("{\"test\": \"data\"}");
    }

    @Test
    void saveBooking_ShouldSaveAndReturnBooking() {
        // Arrange
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        Booking result = bookingDAL.saveBooking(testBooking);

        // Assert
        assertNotNull(result);
        assertEquals(testBooking, result);
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void getBookingsByTripIdAndUserId_ShouldReturnBookings() {
        // Arrange
        List<Booking> expectedBookings = List.of(testBooking);
        when(bookingRepository.findByTripIdAndUser_Id(1L, 1L)).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingDAL.getBookingsByTripIdAndUserId(1L, 1L);

        // Assert
        assertEquals(expectedBookings, result);
        verify(bookingRepository, times(1)).findByTripIdAndUser_Id(1L, 1L);
    }

    @Test
    void getBookingsByUserId_ShouldReturnUserBookings() {
        // Arrange
        List<Booking> expectedBookings = List.of(testBooking);
        when(bookingRepository.findByUser_Id(1L)).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingDAL.getBookingsByUserId(1L);

        // Assert
        assertEquals(expectedBookings, result);
        verify(bookingRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    void getBookingById_WhenExists_ShouldReturnBooking() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        // Act
        Optional<Booking> result = bookingDAL.getBookingById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBooking, result.get());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getBookingById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Booking> result = bookingDAL.getBookingById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(bookingRepository, times(1)).findById(999L);
    }

    @Test
    void deleteBooking_ShouldDeleteBooking() {
        // Arrange
        doNothing().when(bookingRepository).deleteById(1L);

        // Act
        bookingDAL.deleteBooking(1L);

        // Assert
        verify(bookingRepository, times(1)).deleteById(1L);
    }
}

