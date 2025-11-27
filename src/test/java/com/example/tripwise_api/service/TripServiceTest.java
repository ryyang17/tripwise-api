package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.TripDAL;
import com.example.tripwise_api.service.exception.TripNotFoundException;
import com.example.tripwise_api.model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripDAL tripDAL;

    @InjectMocks
    private TripService tripService;

    private Trip validTrip;

    @BeforeEach
    void setUp() {
        validTrip = new Trip();
        validTrip.setTripId(1);
        validTrip.setUserId(1L);
        validTrip.setTripName("Valid Trip");
        validTrip.setStartDate(LocalDate.of(2025, 3, 1));
        validTrip.setEndDate(LocalDate.of(2025, 3, 5));
        validTrip.setDestination("Amsterdam");
        validTrip.setDescription("Valid description");
        validTrip.setTransportation("Car");
        validTrip.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void getAllTrips_ShouldReturnAllTrips() {
        // Arrange
        when(tripDAL.getAllTrips()).thenReturn(List.of(validTrip));

        // Act
        List<Trip> trips = tripService.getAllTrips();

        // Assert
        assertEquals(1, trips.size());
        assertEquals(validTrip, trips.get(0));
        verify(tripDAL, times(1)).getAllTrips();
    }

    @Test
    void getTripById_WhenTripExists_ShouldReturnTrip() {
        // Arrange
        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));

        // Act
        Optional<Trip> maybeTrip = tripService.getTripById(1);

        // Assert
        assertTrue(maybeTrip.isPresent());
        assertEquals(validTrip, maybeTrip.get());
        verify(tripDAL, times(1)).getTripById(1);
    }

    @Test
    void createTrip_ServiceLayer_ShouldPassTripToDALAndReturnSaved() {
        // Arrange
        when(tripDAL.saveTrip(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Trip toCreate = new Trip();
        toCreate.setUserId(validTrip.getUserId());
        toCreate.setTripName(validTrip.getTripName());
        toCreate.setStartDate(validTrip.getStartDate());
        toCreate.setEndDate(validTrip.getEndDate());
        toCreate.setDestination(validTrip.getDestination());
        toCreate.setDescription(validTrip.getDescription());
        toCreate.setTransportation(validTrip.getTransportation());

        // Act
        Trip saved = tripService.createTrip(toCreate);

        // Assert
        assertNotNull(saved);
        verify(tripDAL, times(1)).saveTrip(toCreate);
        assertEquals(toCreate.getTripName(), saved.getTripName());
        assertEquals(toCreate.getUserId(), saved.getUserId());
        assertEquals(toCreate.getStartDate(), saved.getStartDate());
        assertEquals(toCreate.getEndDate(), saved.getEndDate());
    }

    @Test
    void createTrip_WithInvalidData_ShouldThrowIllegalArgumentException() {
        // Arrange
        Trip invalidName = new Trip();
        invalidName.setUserId(1L);
        invalidName.setTripName(""); // empty name

        Trip invalidUserId = new Trip();
        invalidUserId.setUserId(null); // missing user id
        invalidUserId.setTripName("Valid Name");
        invalidUserId.setStartDate(LocalDate.of(2025, 3, 1));
        invalidUserId.setEndDate(LocalDate.of(2025, 3, 5));

        // Act & Assert
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> tripService.createTrip(invalidName));
        assertEquals("Trip name cannot be empty", ex1.getMessage());
        verify(tripDAL, never()).saveTrip(any());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> tripService.createTrip(invalidUserId));
        assertEquals("User ID is required", ex2.getMessage());
        verify(tripDAL, never()).saveTrip(any());
    }

    @Test
    void createTrip_WithNullStartDate_ShouldThrowIllegalArgumentException() {
        // Arrange
        Trip t = new Trip();
        t.setUserId(1L);
        t.setTripName("Name");
        t.setStartDate(null);
        t.setEndDate(LocalDate.of(2025, 3, 5));

        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> tripService.createTrip(t));

        // Assert
        assertEquals("Start date is required", ex.getMessage());
        verify(tripDAL, never()).saveTrip(any());
    }

    @Test
    void createTrip_WithNullEndDate_ShouldThrowIllegalArgumentException() {
        // Arrange
        Trip t = new Trip();
        t.setUserId(1L);
        t.setTripName("Name");
        t.setStartDate(LocalDate.of(2025, 3, 1));
        t.setEndDate(null);

        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> tripService.createTrip(t));

        // Assert
        assertEquals("End date is required", ex.getMessage());
        verify(tripDAL, never()).saveTrip(any());
    }

    @Test
    void createTrip_WithStartDateAfterEndDate_ShouldThrowIllegalArgumentException() {
        // Arrange
        Trip t = new Trip();
        t.setUserId(1L);
        t.setTripName("Name");
        t.setStartDate(LocalDate.of(2025, 4, 10));
        t.setEndDate(LocalDate.of(2025, 4, 5));

        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> tripService.createTrip(t));

        // Assert
        assertEquals("Start date cannot be after end date", ex.getMessage());
        verify(tripDAL, never()).saveTrip(any());
    }

    @Test
    void getTripsByUserId_ShouldReturnUserTrips() {
        // Arrange
        when(tripDAL.getTripsByUserId(1L)).thenReturn(List.of(validTrip));

        // Act
        List<Trip> trips = tripService.getTripsByUserId(1L);

        // Assert
        assertEquals(1, trips.size());
        assertEquals(validTrip, trips.get(0));
        verify(tripDAL, times(1)).getTripsByUserId(1L);
    }

    @Test
    void updateTrip_WhenTripDoesNotExist_ShouldThrowException() {
        // Arrange
        when(tripDAL.getTripById(999)).thenReturn(Optional.empty());

        // Act
        TripNotFoundException ex = assertThrows(TripNotFoundException.class, () -> tripService.updateTrip(999, validTrip));

        // Assert
        assertEquals("Trip not found with id: 999", ex.getMessage());
        verify(tripDAL, times(1)).getTripById(999);
        verify(tripDAL, never()).saveTrip(any());
    }

    @Test
    void updateTrip_WhenTripExists_ShouldUpdateAndSave() {
        // Arrange
        Trip existing = new Trip();
        existing.setTripId(1);
        existing.setUserId(1L);
        existing.setTripName("Old");

        Trip updates = new Trip();
        updates.setUserId(1L);
        updates.setTripName("New");
        updates.setStartDate(LocalDate.of(2025, 4, 1));
        updates.setEndDate(LocalDate.of(2025, 4, 5));

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(existing));
        when(tripDAL.saveTrip(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Trip updatedTrip = tripService.updateTrip(1, updates);

        // Assert
        assertEquals("New", existing.getTripName());
        assertEquals(existing, updatedTrip);
        verify(tripDAL, times(1)).getTripById(1);
        verify(tripDAL, times(1)).saveTrip(existing);
    }

    @Test
    void deleteTrip_WhenTripExists_ShouldCallDeleteOnDAL() {
        // Arrange
        when(tripDAL.existsById(1)).thenReturn(true);

        // Act
        tripService.deleteTrip(1);

        // Assert
        verify(tripDAL, times(1)).existsById(1);
        verify(tripDAL, times(1)).deleteTrip(1);
    }

    @Test
    void deleteTrip_WhenTripDoesNotExist_ShouldThrow() {
        // Arrange
        when(tripDAL.existsById(999)).thenReturn(false);

        // Act
        TripNotFoundException ex = assertThrows(TripNotFoundException.class, () -> tripService.deleteTrip(999));

        // Assert
        assertEquals("Trip not found with id: 999", ex.getMessage());
        verify(tripDAL, times(1)).existsById(999);
        verify(tripDAL, never()).deleteTrip(999);
    }
}
