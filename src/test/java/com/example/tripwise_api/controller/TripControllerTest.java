package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.mapper.TripMapper;
import com.example.tripwise_api.dto.mapper.TripParticipantMapper;
import com.example.tripwise_api.dto.request.CreateTripRequest;
import com.example.tripwise_api.dto.request.UpdateTripRequest;
import com.example.tripwise_api.dto.response.TripResponse;
import com.example.tripwise_api.exception.TripNotFoundException;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.service.TripParticipantService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripControllerTest {

    @Mock
    private TripService tripService;

    @Mock
    private TripParticipantService tripParticipantService;

    @Mock
    private TripMapper tripMapper;

    @Mock
    private TripParticipantMapper tripParticipantMapper;

    @InjectMocks
    private TripController tripController;

    private Trip testTrip;
    private TripResponse testTripResponse;
    private CreateTripRequest createTripRequest;
    private UpdateTripRequest updateTripRequest;

    @BeforeEach
    void setUp() {
        testTrip = new Trip();
        testTrip.setTripId(1);
        testTrip.setUserId(1L);
        testTrip.setTripName("Test Trip");
        testTrip.setStartDate(LocalDate.of(2025, 3, 1));
        testTrip.setEndDate(LocalDate.of(2025, 3, 5));
        testTrip.setDestination("Amsterdam");
        testTrip.setDescription("Test description");
        testTrip.setTransportation("Car");
        testTrip.setCreatedDate(LocalDateTime.now());

        testTripResponse = new TripResponse();
        testTripResponse.setTripId(1);
        testTripResponse.setUserId(1L);
        testTripResponse.setTripName("Test Trip");
        testTripResponse.setStartDate(LocalDate.of(2025, 3, 1));
        testTripResponse.setEndDate(LocalDate.of(2025, 3, 5));
        testTripResponse.setDestination("Amsterdam");
        testTripResponse.setDescription("Test description");
        testTripResponse.setTransportation("Car");

        createTripRequest = new CreateTripRequest();
        createTripRequest.setUserId(1L);
        createTripRequest.setTripName("New Trip");
        createTripRequest.setStartDate(LocalDate.of(2025, 4, 1));
        createTripRequest.setEndDate(LocalDate.of(2025, 4, 5));
        createTripRequest.setDestination("Rotterdam");
        createTripRequest.setDescription("New description");
        createTripRequest.setTransportation("Train");

        updateTripRequest = new UpdateTripRequest();
        updateTripRequest.setTripName("Updated Trip");
        updateTripRequest.setStartDate(LocalDate.of(2025, 5, 1));
        updateTripRequest.setEndDate(LocalDate.of(2025, 5, 5));
        updateTripRequest.setDestination("Utrecht");
        updateTripRequest.setDescription("Updated description");
        updateTripRequest.setTransportation("Bus");
    }

    @Test
    void getAllTrips_ShouldReturnListOfTrips() {
        // Arrange
        List<Trip> trips = List.of(testTrip);
        List<TripResponse> tripResponses = List.of(testTripResponse);

        when(tripService.getAllTrips()).thenReturn(trips);
        when(tripMapper.toResponseList(trips)).thenReturn(tripResponses);

        // Act
        ResponseEntity<List<TripResponse>> response = tripController.getAllTrips();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tripResponses, response.getBody());
        verify(tripService, times(1)).getAllTrips();
        verify(tripMapper, times(1)).toResponseList(trips);
    }

    @Test
    void getTripsByUserId_ShouldReturnUserTrips() {
        // Arrange
        List<Trip> trips = List.of(testTrip);
        List<TripResponse> tripResponses = List.of(testTripResponse);

        when(tripService.getTripsByUserId(1L)).thenReturn(trips);
        when(tripMapper.toResponseList(trips)).thenReturn(tripResponses);

        // Act
        ResponseEntity<List<TripResponse>> response = tripController.getTripsByUserId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tripResponses, response.getBody());
        verify(tripService, times(1)).getTripsByUserId(1L);
        verify(tripMapper, times(1)).toResponseList(trips);
    }

    @Test
    void getTripById_WhenTripExists_ShouldReturnTrip() {
        // Arrange
        when(tripService.getTripById(1)).thenReturn(Optional.of(testTrip));
        when(tripMapper.toResponse(testTrip)).thenReturn(testTripResponse);

        // Act
        ResponseEntity<TripResponse> response = tripController.getTripById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTripResponse, response.getBody());
        verify(tripService, times(1)).getTripById(1);
        verify(tripMapper, times(1)).toResponse(testTrip);
    }

    @Test
    void getTripById_WhenTripDoesNotExist_ShouldReturn404() {
        // Arrange
        when(tripService.getTripById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<TripResponse> response = tripController.getTripById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(tripService, times(1)).getTripById(999);
        verify(tripMapper, never()).toResponse(any(Trip.class));
    }

    @Test
    void createTrip_WithValidRequest_ShouldCreateTrip() {
        // Arrange
        Trip newTrip = new Trip();
        newTrip.setTripId(2);
        newTrip.setUserId(1L);
        newTrip.setTripName("New Trip");

        TripResponse newTripResponse = new TripResponse();
        newTripResponse.setTripId(2);
        newTripResponse.setUserId(1L);
        newTripResponse.setTripName("New Trip");

        when(tripService.createTrip(any(Trip.class))).thenReturn(newTrip);
        when(tripMapper.toResponse(newTrip)).thenReturn(newTripResponse);

        // Act
        ResponseEntity<TripResponse> response = tripController.createTrip(createTripRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newTripResponse, response.getBody());
        verify(tripService, times(1)).createTrip(any(Trip.class));
        verify(tripMapper, times(1)).toResponse(newTrip);
    }

    @Test
    void updateTrip_WhenTripExists_ShouldUpdateTrip() {
        // Arrange
        Trip updatedTrip = new Trip();
        updatedTrip.setTripId(1);
        updatedTrip.setTripName("Updated Trip");

        TripResponse updatedTripResponse = new TripResponse();
        updatedTripResponse.setTripId(1);
        updatedTripResponse.setTripName("Updated Trip");

        when(tripService.getTripById(1)).thenReturn(Optional.of(testTrip));
        when(tripService.updateTrip(anyInt(), any(Trip.class))).thenReturn(updatedTrip);
        when(tripMapper.toResponse(updatedTrip)).thenReturn(updatedTripResponse);

        // Act
        ResponseEntity<TripResponse> response = tripController.updateTrip(1, updateTripRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTripResponse, response.getBody());
        verify(tripService, times(1)).getTripById(1);
        verify(tripService, times(1)).updateTrip(anyInt(), any(Trip.class));
        verify(tripMapper, times(1)).toResponse(updatedTrip);
    }

    @Test
    void updateTrip_WhenTripDoesNotExist_ShouldReturn404() {
        // Arrange
        when(tripService.getTripById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<TripResponse> response = tripController.updateTrip(999, updateTripRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(tripService, times(1)).getTripById(999);
        verify(tripService, never()).updateTrip(anyInt(), any(Trip.class));
    }

    @Test
    void deleteTrip_WhenTripExists_ShouldDeleteTrip() {
        // Arrange
        doNothing().when(tripService).deleteTrip(1);

        // Act
        ResponseEntity<Void> response = tripController.deleteTrip(1);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(tripService, times(1)).deleteTrip(1);
    }

    @Test
    void deleteTrip_WhenTripDoesNotExist_ShouldThrowException() {
        // Arrange
        doThrow(new TripNotFoundException("Trip not found with id: 999"))
                .when(tripService).deleteTrip(999);

        // Act & Assert
        assertThrows(TripNotFoundException.class, () -> tripController.deleteTrip(999));
        verify(tripService, times(1)).deleteTrip(999);
    }
}
