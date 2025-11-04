package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.TripParticipantDAL;
import com.example.tripwise_api.model.TripParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripParticipantServiceTest {

    @Mock
    private TripParticipantDAL dal;

    @InjectMocks
    private TripParticipantService tripParticipantService;

    private TripParticipant validParticipant;

    @BeforeEach
    void setUp() {
        validParticipant = new TripParticipant();
        validParticipant.setId(1); // Changed from setParticipantId to setId
        validParticipant.setTripId(1);
        validParticipant.setUserId(100L);
        validParticipant.setRole("MEMBER");
        validParticipant.setJoinedDate(java.time.LocalDateTime.now());
    }

    @Test
    void addParticipant_WithValidParticipant_ShouldAddParticipant() {
        // Arrange
        when(dal.saveParticipant(any(TripParticipant.class))).thenReturn(validParticipant);

        // Act
        TripParticipant result = tripParticipantService.addParticipant(validParticipant);

        // Assert
        assertEquals(validParticipant, result);
        verify(dal, times(1)).saveParticipant(validParticipant);
    }

    @Test
    void addParticipant_WithNullParticipant_ShouldCallDAL() {
        // Arrange
        when(dal.saveParticipant(null)).thenReturn(null);

        // Act
        TripParticipant result = tripParticipantService.addParticipant(null);

        // Assert
        assertNull(result);
        verify(dal, times(1)).saveParticipant(null);
    }

    @Test
    void getParticipantsByTripId_ShouldReturnTripParticipants() {
        // Arrange
        List<TripParticipant> expectedParticipants = List.of(validParticipant);
        when(dal.getParticipantsByTripId(1)).thenReturn(expectedParticipants);

        // Act
        List<TripParticipant> result = tripParticipantService.getParticipantsByTripId(1);

        // Assert
        assertEquals(expectedParticipants, result);
        verify(dal, times(1)).getParticipantsByTripId(1);
    }

    @Test
    void getParticipantsByTripId_WithNoParticipants_ShouldReturnEmptyList() {
        // Arrange
        when(dal.getParticipantsByTripId(999)).thenReturn(Collections.emptyList());

        // Act
        List<TripParticipant> result = tripParticipantService.getParticipantsByTripId(999);

        // Assert
        assertTrue(result.isEmpty());
        verify(dal, times(1)).getParticipantsByTripId(999);
    }

    @Test
    void getParticipantsByTripId_WithMultipleParticipants_ShouldReturnAllParticipants() {
        // Arrange
        TripParticipant participant1 = new TripParticipant();
        participant1.setId(1); // Changed from setParticipantId to setId
        participant1.setUserId(100L);
        participant1.setRole("ORGANIZER");

        TripParticipant participant2 = new TripParticipant();
        participant2.setId(2); // Changed from setParticipantId to setId
        participant2.setUserId(101L);
        participant2.setRole("MEMBER");

        List<TripParticipant> participants = List.of(participant1, participant2);
        when(dal.getParticipantsByTripId(1)).thenReturn(participants);

        // Act
        List<TripParticipant> result = tripParticipantService.getParticipantsByTripId(1);

        // Assert
        assertEquals(2, result.size());
        assertEquals(participants, result);
        verify(dal, times(1)).getParticipantsByTripId(1);
    }

    @Test
    void getParticipantById_WhenParticipantExists_ShouldReturnParticipant() {
        // Arrange
        when(dal.getParticipantById(1)).thenReturn(Optional.of(validParticipant));

        // Act
        Optional<TripParticipant> result = tripParticipantService.getParticipantById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validParticipant, result.get());
        verify(dal, times(1)).getParticipantById(1);
    }

    @Test
    void getParticipantById_WhenParticipantDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(dal.getParticipantById(999)).thenReturn(Optional.empty());

        // Act
        Optional<TripParticipant> result = tripParticipantService.getParticipantById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(dal, times(1)).getParticipantById(999);
    }

    @Test
    void getParticipantById_WithNullId_ShouldCallDAL() {
        // Arrange
        when(dal.getParticipantById(null)).thenReturn(Optional.empty());

        // Act
        Optional<TripParticipant> result = tripParticipantService.getParticipantById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(dal, times(1)).getParticipantById(null);
    }

    @Test
    void removeParticipant_WithValidId_ShouldRemoveParticipant() {
        // Arrange
        doNothing().when(dal).deleteParticipant(1);

        // Act
        tripParticipantService.removeParticipant(1);

        // Assert
        verify(dal, times(1)).deleteParticipant(1);
    }

    @Test
    void removeParticipant_WithNullId_ShouldCallDAL() {
        // Arrange
        doNothing().when(dal).deleteParticipant(null);

        // Act
        tripParticipantService.removeParticipant(null);

        // Assert
        verify(dal, times(1)).deleteParticipant(null);
    }

    @Test
    void removeParticipant_WithNonExistentId_ShouldCallDAL() {
        // Arrange
        doNothing().when(dal).deleteParticipant(999);

        // Act
        tripParticipantService.removeParticipant(999);

        // Assert
        verify(dal, times(1)).deleteParticipant(999);
    }
}
