package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.repository.TripRepository;
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
class TripDALTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripDAL tripDAL;

    private Trip testTrip;

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
    }

    @Test
    void getAllTrips_ShouldReturnAllTrips() {
        // Arrange
        List<Trip> expectedTrips = List.of(testTrip);
        when(tripRepository.findAll()).thenReturn(expectedTrips);

        // Act
        List<Trip> result = tripDAL.getAllTrips();

        // Assert
        assertEquals(expectedTrips, result);
        verify(tripRepository, times(1)).findAll();
    }

    @Test
    void getTripById_WhenTripExists_ShouldReturnTrip() {
        // Arrange
        when(tripRepository.findById(1)).thenReturn(Optional.of(testTrip));

        // Act
        Optional<Trip> result = tripDAL.getTripById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTrip, result.get());
        verify(tripRepository, times(1)).findById(1);
    }

    @Test
    void getTripById_WhenTripDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(tripRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Trip> result = tripDAL.getTripById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(tripRepository, times(1)).findById(999);
    }

    @Test
    void getTripsByUserId_ShouldReturnUserTrips() {
        // Arrange
        List<Trip> expectedTrips = List.of(testTrip);
        when(tripRepository.findByUserId(1L)).thenReturn(expectedTrips);

        // Act
        List<Trip> result = tripDAL.getTripsByUserId(1L);

        // Assert
        assertEquals(expectedTrips, result);
        verify(tripRepository, times(1)).findByUserId(1L);
    }

    @Test
    void saveTrip_ShouldReturnSavedTrip() {
        // Arrange
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

        // Act
        Trip result = tripDAL.saveTrip(testTrip);

        // Assert
        assertEquals(testTrip, result);
        verify(tripRepository, times(1)).save(testTrip);
    }

    @Test
    void deleteTrip_ShouldCallRepositoryDelete() {
        // Act
        tripDAL.deleteTrip(1);

        // Assert
        verify(tripRepository, times(1)).deleteById(1);
    }

    @Test
    void existsById_WhenTripExists_ShouldReturnTrue() {
        // Arrange
        when(tripRepository.existsById(1)).thenReturn(true);

        // Act
        boolean result = tripDAL.existsById(1);

        // Assert
        assertTrue(result);
        verify(tripRepository, times(1)).existsById(1);
    }

    @Test
    void existsById_WhenTripDoesNotExist_ShouldReturnFalse() {
        // Arrange
        when(tripRepository.existsById(999)).thenReturn(false);

        // Act
        boolean result = tripDAL.existsById(999);

        // Assert
        assertFalse(result);
        verify(tripRepository, times(1)).existsById(999);
    }
}
