package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.TripDAL;
import com.example.tripwise_api.service.exception.TripNotFoundException;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.model.User;
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

    @Mock
    private TripParticipantService tripParticipantService;

    @Mock
    private UserService userService;

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
        when(userService.getUserById(1L)).thenReturn(Optional.of(new User()));
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

    @Test
    void createTrip_ShouldAddCreatorAsOwnerParticipant() {
        // Arrange
        User creator = new User();
        creator.setId(1L);
        creator.setUsername("creator");

        when(tripDAL.saveTrip(any(Trip.class))).thenReturn(validTrip);
        when(userService.getUserById(1L)).thenReturn(Optional.of(creator));
        when(tripParticipantService.addParticipant(any())).thenReturn(null);

        // Act
        Trip result = tripService.createTrip(validTrip);

        // Assert
        assertNotNull(result);
        verify(userService, times(1)).getUserById(1L);
        verify(tripParticipantService, times(1)).addParticipant(any());
        verify(tripDAL, times(1)).saveTrip(validTrip);
    }

    @Test
    void createTrip_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(tripDAL.saveTrip(any(Trip.class))).thenReturn(validTrip);
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tripService.createTrip(validTrip)
        );

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(tripDAL, times(1)).saveTrip(validTrip);
        verify(userService, times(1)).getUserById(1L);
        verify(tripParticipantService, never()).addParticipant(any());
    }

    @Test
    void deleteTrip_ShouldDeleteParticipantsFirst() {
        // Arrange
        when(tripDAL.existsById(1)).thenReturn(true);
        doNothing().when(tripParticipantService).deleteParticipantsByTripId(1);
        doNothing().when(tripDAL).deleteTrip(1);

        // Act
        tripService.deleteTrip(1);

        // Assert
        verify(tripParticipantService, times(1)).deleteParticipantsByTripId(1);
        verify(tripDAL, times(1)).deleteTrip(1);
    }

    @Test
    void getTripById_WhenTripDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(tripDAL.getTripById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Trip> result = tripService.getTripById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(tripDAL, times(1)).getTripById(999);
    }

    @Test
    void getTripsByUserId_WhenNoTrips_ShouldReturnEmptyList() {
        // Arrange
        when(tripDAL.getTripsByUserId(999L)).thenReturn(List.of());

        // Act
        List<Trip> result = tripService.getTripsByUserId(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(tripDAL, times(1)).getTripsByUserId(999L);
    }

    @Test
    void updateTrip_WithInvalidData_ShouldThrowException() {
        // Arrange
        Trip existing = new Trip();
        existing.setTripId(1);

        Trip invalidUpdate = new Trip();
        invalidUpdate.setTripName(""); // Invalid empty name
        invalidUpdate.setUserId(1L);
        invalidUpdate.setStartDate(LocalDate.of(2025, 3, 1));
        invalidUpdate.setEndDate(LocalDate.of(2025, 3, 5));

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(existing));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tripService.updateTrip(1, invalidUpdate)
        );

        assertEquals("Trip name cannot be empty", exception.getMessage());
        verify(tripDAL, times(1)).getTripById(1);
        verify(tripDAL, never()).saveTrip(any());
    }

    @Test
    void updateTrip_WithStartAfterEnd_ShouldThrowException() {
        // Arrange
        Trip existing = new Trip();
        existing.setTripId(1);

        Trip invalidUpdate = new Trip();
        invalidUpdate.setTripName("Valid Name");
        invalidUpdate.setUserId(1L);
        invalidUpdate.setStartDate(LocalDate.of(2025, 3, 10));
        invalidUpdate.setEndDate(LocalDate.of(2025, 3, 5));

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(existing));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tripService.updateTrip(1, invalidUpdate)
        );

        assertEquals("Start date cannot be after end date", exception.getMessage());
        verify(tripDAL, times(1)).getTripById(1);
        verify(tripDAL, never()).saveTrip(any());
    }

    @Test
    void updateTrip_ShouldUpdateAllFields() {
        // Arrange
        Trip existing = new Trip();
        existing.setTripId(1);
        existing.setUserId(1L);
        existing.setTripName("Old Name");
        existing.setDestination("Old Dest");
        existing.setDescription("Old Desc");
        existing.setTransportation("Old Trans");

        Trip updates = new Trip();
        updates.setUserId(2L);
        updates.setTripName("New Name");
        updates.setStartDate(LocalDate.of(2025, 5, 1));
        updates.setEndDate(LocalDate.of(2025, 5, 10));
        updates.setDestination("New Dest");
        updates.setDescription("New Desc");
        updates.setTransportation("New Trans");

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(existing));
        when(tripDAL.saveTrip(any(Trip.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Trip result = tripService.updateTrip(1, updates);

        // Assert
        assertEquals(2L, result.getUserId());
        assertEquals("New Name", result.getTripName());
        assertEquals(LocalDate.of(2025, 5, 1), result.getStartDate());
        assertEquals(LocalDate.of(2025, 5, 10), result.getEndDate());
        assertEquals("New Dest", result.getDestination());
        assertEquals("New Desc", result.getDescription());
        assertEquals("New Trans", result.getTransportation());
        verify(tripDAL, times(1)).saveTrip(existing);
    }

    @Test
    void createTrip_WithWhitespaceOnlyName_ShouldThrowException() {
        // Arrange
        Trip trip = new Trip();
        trip.setTripName("   "); // Whitespace only
        trip.setUserId(1L);
        trip.setStartDate(LocalDate.of(2025, 3, 1));
        trip.setEndDate(LocalDate.of(2025, 3, 5));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tripService.createTrip(trip)
        );

        assertEquals("Trip name cannot be empty", exception.getMessage());
        verify(tripDAL, never()).saveTrip(any());
    }
}
