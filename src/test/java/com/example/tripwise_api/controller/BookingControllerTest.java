package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.request.CreateBookingRequest;
import com.example.tripwise_api.model.Booking;
import com.example.tripwise_api.model.BookingType;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.BookingService;
import com.example.tripwise_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookingController bookingController;

    private User testUser;
    private Booking testBooking;
    private CreateBookingRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

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

        createRequest = new CreateBookingRequest();
        createRequest.setBookingType("FLIGHT");
        createRequest.setProvider("Test Airline");
        createRequest.setBookingReference("REF123");
        createRequest.setPrice(new BigDecimal("500.00"));
        createRequest.setCurrency("USD");
        createRequest.setRawPayload("{\"test\": \"data\"}");
    }

    private void setupAuthentication(boolean authenticated) {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(authenticated);
        if (authenticated) {
            org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(testUser);
            when(authentication.getName()).thenReturn("testuser");
        } else {
            org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn("anonymousUser");
        }
    }

    @Test
    void createBooking_WithValidData_ShouldReturnCreated() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(bookingService.createBookingForUser(any(User.class), any(Booking.class), eq(1L)))
            .thenReturn(testBooking);

        // Act
        ResponseEntity<?> response = bookingController.createBooking(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(bookingService, times(1)).createBookingForUser(any(User.class), any(Booking.class), eq(1L));
    }

    @Test
    void createBooking_WithoutAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthentication(false);

        // Act
        ResponseEntity<?> response = bookingController.createBooking(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(bookingService, never()).createBookingForUser(any(), any(), anyLong());
    }

    @Test
    void createBooking_WithInvalidRawPayload_ShouldReturnBadRequest() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        createRequest.setRawPayload("");

        // Act
        ResponseEntity<?> response = bookingController.createBooking(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(bookingService, never()).createBookingForUser(any(), any(), anyLong());
    }

    @Test
    void getBookingsByTrip_WithAuthentication_ShouldReturnBookings() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(bookingService.getBookingsByTripId(1L, 1L)).thenReturn(List.of(testBooking));

        // Act
        ResponseEntity<?> response = bookingController.getBookingsByTrip(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(bookingService, times(1)).getBookingsByTripId(1L, 1L);
    }

    @Test
    void getBookingsByTrip_WithoutAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthentication(false);

        // Act
        ResponseEntity<?> response = bookingController.getBookingsByTrip(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(bookingService, never()).getBookingsByTripId(anyLong(), anyLong());
    }

    @Test
    void getBookingById_WithAuthentication_ShouldReturnBooking() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(bookingService.getBookingById(1L)).thenReturn(Optional.of(testBooking));

        // Act
        ResponseEntity<?> response = bookingController.getBookingById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingService, times(1)).getBookingById(1L);
    }

    @Test
    void getBookingById_WithoutAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthentication(false);

        // Act
        ResponseEntity<?> response = bookingController.getBookingById(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(bookingService, never()).getBookingById(anyLong());
    }
}
