package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateTripRequest;
import com.example.tripwise_api.dto.request.UpdateTripRequest;
import com.example.tripwise_api.dto.response.TripResponse;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
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
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TripMapperTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private TripMapper tripMapper;

    private Trip trip;
    private User user;
    private CreateTripRequest createRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        trip = new Trip();
        trip.setTripId(1);
        trip.setUserId(1L);
        trip.setTripName("Test Trip");
        trip.setStartDate(LocalDate.of(2025, 3, 1));
        trip.setEndDate(LocalDate.of(2025, 3, 5));
        trip.setDestination("Amsterdam");
        trip.setDescription("Test Description");
        trip.setTransportation("Car");
        trip.setCreatedDate(LocalDateTime.now());

        createRequest = new CreateTripRequest();
        createRequest.setUserId(1L);
        createRequest.setTripName("Test Trip");
        createRequest.setStartDate(LocalDate.of(2025, 3, 1));
        createRequest.setEndDate(LocalDate.of(2025, 3, 5));
        createRequest.setDestination("Amsterdam");
        createRequest.setDescription("Test Description");
        createRequest.setTransportation("Car");
    }

    @Test
    void toResponse_ShouldMapEntityToResponse() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        TripResponse result = tripMapper.toResponse(trip);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTripId());
        assertEquals(1L, result.getUserId());
        assertEquals("Test Trip", result.getTripName());
        assertEquals("Amsterdam", result.getDestination());
        assertEquals("testuser", result.getOwnerName());
        assertEquals("test@example.com", result.getOwnerEmail());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void toResponse_WithNullTrip_ShouldReturnNull() {
        // Act
        TripResponse result = tripMapper.toResponse(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toResponse_WithFutureEndDate_ShouldSetIsActiveTrue() {
        // Arrange
        trip.setEndDate(LocalDate.now().plusDays(10));
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        TripResponse result = tripMapper.toResponse(trip);

        // Assert
        assertTrue(result.getIsActive());
    }

    @Test
    void toResponse_WithPastEndDate_ShouldSetIsActiveFalse() {
        // Arrange
        trip.setEndDate(LocalDate.now().minusDays(10));
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        TripResponse result = tripMapper.toResponse(trip);

        // Assert
        assertFalse(result.getIsActive());
    }

    @Test
    void toResponse_WithoutUser_ShouldNotSetOwnerDetails() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // Act
        TripResponse result = tripMapper.toResponse(trip);

        // Assert
        assertNotNull(result);
        assertNull(result.getOwnerName());
        assertNull(result.getOwnerEmail());
    }

    @Test
    void toResponseList_ShouldMapListOfEntities() {
        // Arrange
        Trip trip2 = new Trip();
        trip2.setTripId(2);
        trip2.setUserId(1L);
        trip2.setTripName("Trip 2");
        trip2.setEndDate(LocalDate.now().plusDays(5));

        List<Trip> trips = List.of(trip, trip2);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        List<TripResponse> result = tripMapper.toResponseList(trips);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTripId());
        assertEquals(2, result.get(1).getTripId());
    }

    @Test
    void toResponseList_WithNullList_ShouldReturnNull() {
        // Act
        List<TripResponse> result = tripMapper.toResponseList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_ShouldMapRequestToEntity() {
        // Act
        Trip result = TripMapper.toEntity(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("Test Trip", result.getTripName());
        assertEquals("Amsterdam", result.getDestination());
        assertEquals(LocalDate.of(2025, 3, 1), result.getStartDate());
        assertEquals(LocalDate.of(2025, 3, 5), result.getEndDate());
    }

    @Test
    void toEntity_WithNullRequest_ShouldReturnNull() {
        // Act
        Trip result = TripMapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    void updateEntity_ShouldUpdateExistingTrip() {
        // Arrange
        UpdateTripRequest updateRequest = new UpdateTripRequest();
        updateRequest.setTripName("Updated Trip");
        updateRequest.setDestination("Paris");

        // Act
        TripMapper.updateEntity(trip, updateRequest);

        // Assert
        assertEquals("Updated Trip", trip.getTripName());
        assertEquals("Paris", trip.getDestination());
    }

    @Test
    void updateEntity_WithAllFields_ShouldUpdateCorrectly() {
        // Arrange
        UpdateTripRequest updateRequest = new UpdateTripRequest();
        updateRequest.setTripName("New Name");
        updateRequest.setStartDate(LocalDate.of(2025, 6, 1));
        updateRequest.setEndDate(LocalDate.of(2025, 6, 10));
        updateRequest.setDestination("Berlin");
        updateRequest.setDescription("New Description");
        updateRequest.setTransportation("train");

        // Act
        TripMapper.updateEntity(trip, updateRequest);

        // Assert
        assertEquals("New Name", trip.getTripName());
        assertEquals(LocalDate.of(2025, 6, 1), trip.getStartDate());
        assertEquals(LocalDate.of(2025, 6, 10), trip.getEndDate());
        assertEquals("Berlin", trip.getDestination());
        assertEquals("New Description", trip.getDescription());
        assertEquals("train", trip.getTransportation());
    }

    @Test
    void updateEntity_WithNullTrip_ShouldNotThrow() {
        // Arrange
        UpdateTripRequest updateRequest = new UpdateTripRequest();
        updateRequest.setTripName("Test");

        // Act & Assert
        assertDoesNotThrow(() -> TripMapper.updateEntity(null, updateRequest));
    }

    @Test
    void updateEntity_WithNullRequest_ShouldNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> TripMapper.updateEntity(trip, null));
    }

    @Test
    void updateEntity_WithPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        String originalDescription = trip.getDescription();
        UpdateTripRequest updateRequest = new UpdateTripRequest();
        updateRequest.setTripName("New Name Only");
        // Other fields are null

        // Act
        TripMapper.updateEntity(trip, updateRequest);

        // Assert
        assertEquals("New Name Only", trip.getTripName());
        assertEquals(originalDescription, trip.getDescription()); // Should remain unchanged
    }

    @Test
    void toResponse_WithNullEndDate_ShouldSetIsActiveFalse() {
        // Arrange
        trip.setEndDate(null);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        TripResponse result = tripMapper.toResponse(trip);

        // Assert
        assertFalse(result.getIsActive());
    }

    @Test
    void toResponse_WithTodayAsEndDate_ShouldSetIsActiveTrue() {
        // Arrange
        trip.setEndDate(LocalDate.now());
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        TripResponse result = tripMapper.toResponse(trip);

        // Assert
        assertTrue(result.getIsActive());
    }

    @Test
    void toResponse_WithNullUserId_ShouldNotFetchUser() {
        // Arrange
        trip.setUserId(null);

        // Act
        TripResponse result = tripMapper.toResponse(trip);

        // Assert
        assertNotNull(result);
        assertNull(result.getOwnerName());
        assertNull(result.getOwnerEmail());
        verify(userService, never()).getUserById(any());
    }

    @Test
    void toResponseList_WithEmptyList_ShouldReturnEmptyList() {
        // Act
        List<TripResponse> result = tripMapper.toResponseList(List.of());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntity_WithAllFields_ShouldMapCorrectly() {
        // Act
        Trip result = TripMapper.toEntity(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("Test Trip", result.getTripName());
        assertEquals(LocalDate.of(2025, 3, 1), result.getStartDate());
        assertEquals(LocalDate.of(2025, 3, 5), result.getEndDate());
        assertEquals("Amsterdam", result.getDestination());
        assertEquals("Test Description", result.getDescription());
        assertEquals("Car", result.getTransportation());
    }

    @Test
    void toEntity_WithMinimalFields_ShouldMapCorrectly() {
        // Arrange
        CreateTripRequest minimalRequest = new CreateTripRequest();
        minimalRequest.setUserId(1L);
        minimalRequest.setTripName("Minimal Trip");
        minimalRequest.setStartDate(LocalDate.now());
        minimalRequest.setEndDate(LocalDate.now().plusDays(1));
        minimalRequest.setDestination("Test");
        minimalRequest.setTransportation("car");

        // Act
        Trip result = TripMapper.toEntity(minimalRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Minimal Trip", result.getTripName());
        assertNull(result.getDescription()); // Optional field
    }

    @Test
    void toResponse_WithAllFieldsPopulated_ShouldMapAllFields() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        TripResponse result = tripMapper.toResponse(trip);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTripId());
        assertEquals(1L, result.getUserId());
        assertEquals("Test Trip", result.getTripName());
        assertEquals(LocalDate.of(2025, 3, 1), result.getStartDate());
        assertEquals(LocalDate.of(2025, 3, 5), result.getEndDate());
        assertEquals("Amsterdam", result.getDestination());
        assertEquals("Test Description", result.getDescription());
        assertEquals("Car", result.getTransportation());
        assertNotNull(result.getCreatedDate());
        assertEquals("testuser", result.getOwnerName());
        assertEquals("test@example.com", result.getOwnerEmail());
    }
}

