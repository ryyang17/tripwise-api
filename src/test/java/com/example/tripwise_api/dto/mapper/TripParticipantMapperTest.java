package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateParticipantRequest;
import com.example.tripwise_api.dto.response.TripParticipantResponse;
import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.model.TripParticipant;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.TripService;
import com.example.tripwise_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripParticipantMapperTest {

    @Mock
    private UserService userService;

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripParticipantMapper tripParticipantMapper;

    private TripParticipant participant;
    private User user;
    private Trip trip;
    private CreateParticipantRequest createRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        trip = new Trip();
        trip.setTripId(100);
        trip.setUserId(1L); // Owner ID
        trip.setTripName("Test Trip");

        participant = new TripParticipant();
        participant.setId(1);
        participant.setTripId(100);
        participant.setUserId(1L);
        participant.setUser(user);
        participant.setRole("participant");
        participant.setJoinedDate(LocalDateTime.now());

        createRequest = new CreateParticipantRequest();
        createRequest.setUserId(1L);
        createRequest.setRole("participant");
    }

    @Test
    void toResponse_ShouldMapEntityToResponse() {
        // Arrange
        when(tripService.getTripById(100)).thenReturn(Optional.of(trip));

        // Act
        TripParticipantResponse result = tripParticipantMapper.toResponse(participant);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getParticipantId());
        assertEquals(1, result.getId()); // Backwards compatibility
        assertEquals(100, result.getTripId());
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUserName());
        assertEquals("test@example.com", result.getUserEmail());
        assertEquals("participant", result.getRole());
        assertNotNull(result.getJoinedDate());
        assertTrue(result.getIsOwner()); // User ID matches trip owner ID
    }

    @Test
    void toResponse_WithNullParticipant_ShouldReturnNull() {
        // Act
        TripParticipantResponse result = tripParticipantMapper.toResponse(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toResponse_WithoutUserRelationship_ShouldFetchUserViaService() {
        // Arrange
        participant.setUser(null);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(tripService.getTripById(100)).thenReturn(Optional.of(trip));

        // Act
        TripParticipantResponse result = tripParticipantMapper.toResponse(participant);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals("test@example.com", result.getUserEmail());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void toResponse_WithUserNotFound_ShouldHandleGracefully() {
        // Arrange
        participant.setUser(null);
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        when(tripService.getTripById(100)).thenReturn(Optional.of(trip));

        // Act
        TripParticipantResponse result = tripParticipantMapper.toResponse(participant);

        // Assert
        assertNotNull(result);
        assertNull(result.getUserName());
        assertNull(result.getUserEmail());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void toResponse_WithTripNotFound_ShouldSetIsOwnerToFalse() {
        // Arrange
        when(tripService.getTripById(100)).thenReturn(Optional.empty());

        // Act
        TripParticipantResponse result = tripParticipantMapper.toResponse(participant);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsOwner());
    }

    @Test
    void toResponse_WithNonOwnerUser_ShouldSetIsOwnerToFalse() {
        // Arrange
        trip.setUserId(999L); // Different from participant's userId
        when(tripService.getTripById(100)).thenReturn(Optional.of(trip));

        // Act
        TripParticipantResponse result = tripParticipantMapper.toResponse(participant);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsOwner());
    }

    @Test
    void toResponse_BothIdFieldsShouldBeInSync() {
        // Arrange
        when(tripService.getTripById(100)).thenReturn(Optional.of(trip));

        // Act
        TripParticipantResponse result = tripParticipantMapper.toResponse(participant);

        // Assert
        assertNotNull(result);
        assertEquals(result.getParticipantId(), result.getId());
    }

    @Test
    void toResponseList_ShouldMapListOfEntities() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        TripParticipant participant2 = new TripParticipant();
        participant2.setId(2);
        participant2.setTripId(100);
        participant2.setUserId(2L);
        participant2.setUser(user2);
        participant2.setRole("viewer");
        participant2.setJoinedDate(LocalDateTime.now());

        List<TripParticipant> participants = List.of(participant, participant2);

        when(tripService.getTripById(100)).thenReturn(Optional.of(trip));

        // Act
        List<TripParticipantResponse> result = tripParticipantMapper.toResponseList(participants);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getParticipantId());
        assertEquals(2, result.get(1).getParticipantId());
        assertEquals("testuser", result.get(0).getUserName());
        assertEquals("user2", result.get(1).getUserName());
    }

    @Test
    void toResponseList_WithNullList_ShouldReturnNull() {
        // Act
        List<TripParticipantResponse> result = tripParticipantMapper.toResponseList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toResponseList_WithEmptyList_ShouldReturnEmptyList() {
        // Act
        List<TripParticipantResponse> result = tripParticipantMapper.toResponseList(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntity_ShouldMapRequestToEntity() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        TripParticipant result = tripParticipantMapper.toEntity(createRequest, 100);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getTripId());
        assertEquals(1L, result.getUserId());
        assertEquals("participant", result.getRole());
        assertNotNull(result.getUser());
        assertEquals("testuser", result.getUser().getUsername());
    }

    @Test
    void toEntity_WithNullRequest_ShouldReturnNull() {
        // Act
        TripParticipant result = tripParticipantMapper.toEntity(null, 100);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_WithUserNotFound_ShouldThrowException() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> tripParticipantMapper.toEntity(createRequest, 100));

        assertTrue(exception.getMessage().contains("User not found with ID: 1"));
    }

    @Test
    void toEntity_WithDifferentRole_ShouldMapCorrectly() {
        // Arrange
        createRequest.setRole("admin");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        TripParticipant result = tripParticipantMapper.toEntity(createRequest, 100);

        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getRole());
    }

    @Test
    void toEntity_WithNullRole_ShouldMapCorrectly() {
        // Arrange
        createRequest.setRole(null);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        TripParticipant result = tripParticipantMapper.toEntity(createRequest, 100);

        // Assert
        assertNotNull(result);
        assertNull(result.getRole());
    }

    @Test
    void toResponse_WithDifferentRoles_ShouldMapCorrectly() {
        // Test with different roles
        String[] roles = {"admin", "participant", "viewer", "editor", null};

        for (String role : roles) {
            // Arrange
            participant.setRole(role);
            when(tripService.getTripById(100)).thenReturn(Optional.of(trip));

            // Act
            TripParticipantResponse result = tripParticipantMapper.toResponse(participant);

            // Assert
            assertNotNull(result);
            assertEquals(role, result.getRole());
        }
    }

    @Test
    void toResponseList_WithMixedOwnerAndNonOwner_ShouldIdentifyCorrectly() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        TripParticipant owner = new TripParticipant();
        owner.setId(1);
        owner.setTripId(100);
        owner.setUserId(1L);
        owner.setUser(user);

        TripParticipant nonOwner = new TripParticipant();
        nonOwner.setId(2);
        nonOwner.setTripId(100);
        nonOwner.setUserId(2L);
        nonOwner.setUser(user2);

        List<TripParticipant> participants = List.of(owner, nonOwner);

        trip.setUserId(1L); // First participant is owner
        when(tripService.getTripById(100)).thenReturn(Optional.of(trip));

        // Act
        List<TripParticipantResponse> result = tripParticipantMapper.toResponseList(participants);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getIsOwner());
        assertFalse(result.get(1).getIsOwner());
    }

    @Test
    void toEntity_WithMultipleUsers_ShouldCreateSeparateEntities() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        CreateParticipantRequest request1 = new CreateParticipantRequest();
        request1.setUserId(1L);
        request1.setRole("admin");

        CreateParticipantRequest request2 = new CreateParticipantRequest();
        request2.setUserId(2L);
        request2.setRole("viewer");

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userService.getUserById(2L)).thenReturn(Optional.of(user2));

        // Act
        TripParticipant result1 = tripParticipantMapper.toEntity(request1, 100);
        TripParticipant result2 = tripParticipantMapper.toEntity(request2, 100);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(1L, result1.getUserId());
        assertEquals(2L, result2.getUserId());
        assertEquals("admin", result1.getRole());
        assertEquals("viewer", result2.getRole());
    }
}

