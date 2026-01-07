package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.mapper.TripMapper;
import com.example.tripwise_api.dto.request.CreateTripRequest;
import com.example.tripwise_api.dto.request.UpdateTripRequest;
import com.example.tripwise_api.dto.response.TripResponse;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripControllerTest {

    @Mock
    private TripService tripService;

    @Mock
    private TripMapper tripMapper;

    @InjectMocks
    private TripController tripController;

    private Trip testTrip;
    private TripResponse tripResponse;
    private CreateTripRequest createRequest;
    private UpdateTripRequest updateRequest;

    @BeforeEach
    void setUp() {
        testTrip = new Trip();
        testTrip.setTripId(1);
        testTrip.setUserId(1L);
        testTrip.setTripName("Test Trip");
        testTrip.setStartDate(LocalDate.of(2025, 3, 1));
        testTrip.setEndDate(LocalDate.of(2025, 3, 5));
        testTrip.setDestination("Amsterdam");

        tripResponse = new TripResponse();
        tripResponse.setTripId(1);
        tripResponse.setTripName("Test Trip");

        createRequest = new CreateTripRequest();
        createRequest.setUserId(1L);
        createRequest.setTripName("Test Trip");
        createRequest.setStartDate(LocalDate.of(2025, 3, 1));
        createRequest.setEndDate(LocalDate.of(2025, 3, 5));
        createRequest.setDestination("Amsterdam");

        updateRequest = new UpdateTripRequest();
        updateRequest.setTripName("Updated Trip");
    }

    @Test
    void getAllTrips_ShouldReturnAllTrips() {
        // Arrange
        when(tripService.getAllTrips()).thenReturn(List.of(testTrip));
        when(tripMapper.toResponseList(anyList())).thenReturn(List.of(tripResponse));

        // Act
        ResponseEntity<List<TripResponse>> response = tripController.getAllTrips();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(tripService, times(1)).getAllTrips();
    }

    @Test
    void getTripsByUserId_ShouldReturnUserTrips() {
        // Arrange
        when(tripService.getTripsByUserId(1L)).thenReturn(List.of(testTrip));
        when(tripMapper.toResponseList(anyList())).thenReturn(List.of(tripResponse));

        // Act
        ResponseEntity<List<TripResponse>> response = tripController.getTripsByUserId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tripService, times(1)).getTripsByUserId(1L);
    }

    @Test
    void getTripById_WhenExists_ShouldReturnTrip() {
        // Arrange
        when(tripService.getTripById(1)).thenReturn(Optional.of(testTrip));
        when(tripMapper.toResponse(testTrip)).thenReturn(tripResponse);

        // Act
        ResponseEntity<TripResponse> response = tripController.getTripById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tripService, times(1)).getTripById(1);
    }

    @Test
    void getTripById_WhenNotExists_ShouldReturnNotFound() {
        // Arrange
        when(tripService.getTripById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<TripResponse> response = tripController.getTripById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(tripService, times(1)).getTripById(999);
    }

    @Test
    void createTrip_WithValidData_ShouldReturnCreated() {
        // Arrange
        when(tripService.createTrip(any(Trip.class))).thenReturn(testTrip);
        when(tripMapper.toResponse(testTrip)).thenReturn(tripResponse);

        // Act
        ResponseEntity<TripResponse> response = tripController.createTrip(createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tripService, times(1)).createTrip(any(Trip.class));
    }

    @Test
    void updateTrip_WhenExists_ShouldReturnUpdatedTrip() {
        // Arrange
        when(tripService.getTripById(1)).thenReturn(Optional.of(testTrip));
        when(tripService.updateTrip(eq(1), any(Trip.class))).thenReturn(testTrip);
        when(tripMapper.toResponse(testTrip)).thenReturn(tripResponse);

        // Act
        ResponseEntity<TripResponse> response = tripController.updateTrip(1, updateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tripService, times(1)).updateTrip(eq(1), any(Trip.class));
    }

    @Test
    void updateTrip_WhenNotExists_ShouldReturnNotFound() {
        // Arrange
        when(tripService.getTripById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<TripResponse> response = tripController.updateTrip(999, updateRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(tripService, never()).updateTrip(anyInt(), any(Trip.class));
    }

    @Test
    void deleteTrip_WhenExists_ShouldReturnNoContent() {
        // Arrange
        when(tripService.getTripById(1)).thenReturn(Optional.of(testTrip));
        doNothing().when(tripService).deleteTrip(1);

        // Act
        ResponseEntity<Void> response = tripController.deleteTrip(1);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(tripService, times(1)).deleteTrip(1);
    }

    @Test
    void deleteTrip_WhenNotExists_ShouldReturnNotFound() {
        // Arrange
        when(tripService.getTripById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Void> response = tripController.deleteTrip(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(tripService, never()).deleteTrip(anyInt());
    }
}
