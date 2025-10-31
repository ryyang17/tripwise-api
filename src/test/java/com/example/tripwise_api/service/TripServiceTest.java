package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.TripDAL;
import com.example.tripwise_api.exception.TripNotFoundException;
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
    private Trip invalidTrip;

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

        invalidTrip = new Trip();
        invalidTrip.setTripId(2);
        invalidTrip.setUserId(null); // Invalid - no user ID
        invalidTrip.setTripName(""); // Invalid - empty name
    }

    @Test
    void getAllTrips_ShouldReturnAllTrips() {
        // Arrange
        List<Trip> expectedTrips = List.of(validTrip);
        when(tripDAL.getAllTrips()).thenReturn(expectedTrips);

        // Act
        List<Trip> result = tripService.getAllTrips();

        // Assert
        assertEquals(expectedTrips, result);
        verify(tripDAL, times(1)).getAllTrips();
    }

    @Test
    void getTripById_WhenTripExists_ShouldReturnTrip() {
        // Arrange
        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));

        // Act
        Optional<Trip> result = tripService.getTripById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validTrip, result.get());
        verify(tripDAL, times(1)).getTripById(1);
    }

    @Test
    void getTripsByUserId_ShouldReturnUserTrips() {
        // Arrange
        List<Trip> expectedTrips = List.of(validTrip);
        when(tripDAL.getTripsByUserId(1L)).thenReturn(expectedTrips);

        // Act
        List<Trip> result = tripService.getTripsByUserId(1L);

        // Assert
        assertEquals(expectedTrips, result);
        verify(tripDAL, times(1)).getTripsByUserId(1L);
    }

    @Test
    void createTrip_WithValidTrip_ShouldCreateTrip() {
        // Arrange
        when(tripDAL.saveTrip(any(Trip.class))).thenReturn(validTrip);

        // Act
        Trip result = tripService.createTrip(validTrip);

        // Assert
        assertEquals(validTrip, result);
        verify(tripDAL, times(1)).saveTrip(validTrip);
    }

    @Test
    void createTrip_WithEmptyTripName_ShouldThrowException() {
        // Arrange
        Trip tripWithEmptyName = new Trip();
        tripWithEmptyName.setUserId(1L);
        tripWithEmptyName.setTripName(""); // Empty name

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tripService.createTrip(tripWithEmptyName)
        );
        assertEquals("Trip name cannot be empty", exception.getMessage());
        verify(tripDAL, never()).saveTrip(any(Trip.class));
    }

    @Test
    void createTrip_WithNullTripName_ShouldThrowException() {
        // Arrange
        Trip tripWithNullName = new Trip();
        tripWithNullName.setUserId(1L);
        tripWithNullName.setTripName(null); // Null name

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tripService.createTrip(tripWithNullName)
        );
        assertEquals("Trip name cannot be empty", exception.getMessage());
        verify(tripDAL, never()).saveTrip(any(Trip.class));
    }

    @Test
    void createTrip_WithNullUserId_ShouldThrowException() {
        // Arrange
        Trip tripWithNullUserId = new Trip();
        tripWithNullUserId.setTripName("Valid Name");
        tripWithNullUserId.setUserId(null); // Null user ID

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tripService.createTrip(tripWithNullUserId)
        );
        assertEquals("User ID is required", exception.getMessage());
        verify(tripDAL, never()).saveTrip(any(Trip.class));
    }

    @Test
    void createTrip_WithStartDateAfterEndDate_ShouldThrowException() {
        // Arrange
        Trip tripWithInvalidDates = new Trip();
        tripWithInvalidDates.setUserId(1L);
        tripWithInvalidDates.setTripName("Valid Name");
        tripWithInvalidDates.setStartDate(LocalDate.of(2025, 3, 10)); // Start after end
        tripWithInvalidDates.setEndDate(LocalDate.of(2025, 3, 5));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tripService.createTrip(tripWithInvalidDates)
        );
        assertEquals("Start date cannot be after end date", exception.getMessage());
        verify(tripDAL, never()).saveTrip(any(Trip.class));
    }

    @Test
    void updateTrip_WhenTripExists_ShouldUpdateTrip() {
        // Arrange
        Trip existingTrip = new Trip();
        existingTrip.setTripId(1);
        existingTrip.setUserId(1L);
        existingTrip.setTripName("Old Name");

        Trip updateDetails = new Trip();
        updateDetails.setUserId(1L);
        updateDetails.setTripName("New Name");
        updateDetails.setStartDate(LocalDate.of(2025, 4, 1));
        updateDetails.setEndDate(LocalDate.of(2025, 4, 5));
        updateDetails.setDestination("Rotterdam");
        updateDetails.setDescription("Updated description");
        updateDetails.setTransportation("Train");

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(existingTrip));
        when(tripDAL.saveTrip(any(Trip.class))).thenReturn(existingTrip);

        // Act
        Trip result = tripService.updateTrip(1, updateDetails);

        // Assert
        assertNotNull(result);
        verify(tripDAL, times(1)).getTripById(1);
        verify(tripDAL, times(1)).saveTrip(existingTrip);

        // Verify that fields were updated
        assertEquals("New Name", existingTrip.getTripName());
        assertEquals("Rotterdam", existingTrip.getDestination());
        assertEquals("Updated description", existingTrip.getDescription());
        assertEquals("Train", existingTrip.getTransportation());
    }

    @Test
    void updateTrip_WhenTripDoesNotExist_ShouldThrowException() {
        // Arrange
        when(tripDAL.getTripById(999)).thenReturn(Optional.empty());

        // Act & Assert
        TripNotFoundException exception = assertThrows(
            TripNotFoundException.class,
            () -> tripService.updateTrip(999, validTrip)
        );
        assertEquals("Trip not found with id: 999", exception.getMessage());
        verify(tripDAL, times(1)).getTripById(999);
        verify(tripDAL, never()).saveTrip(any(Trip.class));
    }

    @Test
    void updateTrip_WithInvalidData_ShouldThrowException() {
        // Arrange
        Trip existingTrip = new Trip();
        existingTrip.setTripId(1);

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(existingTrip));

        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> tripService.updateTrip(1, invalidTrip)
        );
        verify(tripDAL, times(1)).getTripById(1);
        verify(tripDAL, never()).saveTrip(any(Trip.class));
    }

    @Test
    void deleteTrip_WhenTripExists_ShouldDeleteTrip() {
        // Arrange
        when(tripDAL.existsById(1)).thenReturn(true);

        // Act
        tripService.deleteTrip(1);

        // Assert
        verify(tripDAL, times(1)).existsById(1);
        verify(tripDAL, times(1)).deleteTrip(1);
    }

    @Test
    void deleteTrip_WhenTripDoesNotExist_ShouldThrowException() {
        // Arrange
        when(tripDAL.existsById(999)).thenReturn(false);

        // Act & Assert
        TripNotFoundException exception = assertThrows(
            TripNotFoundException.class,
            () -> tripService.deleteTrip(999)
        );
        assertEquals("Trip not found with id: 999", exception.getMessage());
        verify(tripDAL, times(1)).existsById(999);
        verify(tripDAL, never()).deleteTrip(999);
    }
}
