package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.TripParticipant;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.repository.TripParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripParticipantDALTest {

    @Mock
    private TripParticipantRepository tripParticipantRepository;

    @InjectMocks
    private TripParticipantDAL tripParticipantDAL;

    private TripParticipant testParticipant;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testParticipant = new TripParticipant();
        testParticipant.setId(1);
        testParticipant.setTripId(100);
        testParticipant.setUser(testUser);
        testParticipant.setRole("ORGANIZER");
        testParticipant.setJoinedDate(LocalDateTime.now());
    }

    @Test
    void saveParticipant_ShouldSaveAndReturnParticipant() {
        // Arrange
        when(tripParticipantRepository.save(any(TripParticipant.class))).thenReturn(testParticipant);

        // Act
        TripParticipant result = tripParticipantDAL.saveParticipant(testParticipant);

        // Assert
        assertNotNull(result);
        assertEquals(testParticipant, result);
        assertEquals("ORGANIZER", result.getRole());
        assertEquals(100, result.getTripId());
        verify(tripParticipantRepository, times(1)).save(testParticipant);
    }

    @Test
    void saveParticipant_WithNewParticipant_ShouldSaveSuccessfully() {
        // Arrange
        TripParticipant newParticipant = new TripParticipant(200, 2L, "PARTICIPANT");
        newParticipant.setId(2);
        when(tripParticipantRepository.save(any(TripParticipant.class))).thenReturn(newParticipant);

        // Act
        TripParticipant result = tripParticipantDAL.saveParticipant(newParticipant);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals(200, result.getTripId());
        assertEquals("PARTICIPANT", result.getRole());
        verify(tripParticipantRepository, times(1)).save(newParticipant);
    }

    @Test
    void getParticipantsByTripId_ShouldReturnParticipants() {
        // Arrange
        TripParticipant participant2 = new TripParticipant(100, 2L, "PARTICIPANT");
        List<TripParticipant> expectedParticipants = List.of(testParticipant, participant2);
        when(tripParticipantRepository.findByTripId(100)).thenReturn(expectedParticipants);

        // Act
        List<TripParticipant> result = tripParticipantDAL.getParticipantsByTripId(100);

        // Assert
        assertEquals(expectedParticipants, result);
        assertEquals(2, result.size());
        verify(tripParticipantRepository, times(1)).findByTripId(100);
    }

    @Test
    void getParticipantsByTripId_WhenNoParticipants_ShouldReturnEmptyList() {
        // Arrange
        when(tripParticipantRepository.findByTripId(999)).thenReturn(List.of());

        // Act
        List<TripParticipant> result = tripParticipantDAL.getParticipantsByTripId(999);

        // Assert
        assertTrue(result.isEmpty());
        verify(tripParticipantRepository, times(1)).findByTripId(999);
    }

    @Test
    void getParticipantsByTripIdWithUserDetails_ShouldReturnParticipantsWithUsers() {
        // Arrange
        List<TripParticipant> expectedParticipants = List.of(testParticipant);
        when(tripParticipantRepository.findByTripIdWithUser(100)).thenReturn(expectedParticipants);

        // Act
        List<TripParticipant> result = tripParticipantDAL.getParticipantsByTripIdWithUserDetails(100);

        // Assert
        assertEquals(expectedParticipants, result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getUser());
        assertEquals("testuser", result.get(0).getUser().getUsername());
        verify(tripParticipantRepository, times(1)).findByTripIdWithUser(100);
    }

    @Test
    void getParticipantsByTripIdWithUserDetails_WhenNoParticipants_ShouldReturnEmptyList() {
        // Arrange
        when(tripParticipantRepository.findByTripIdWithUser(999)).thenReturn(List.of());

        // Act
        List<TripParticipant> result = tripParticipantDAL.getParticipantsByTripIdWithUserDetails(999);

        // Assert
        assertTrue(result.isEmpty());
        verify(tripParticipantRepository, times(1)).findByTripIdWithUser(999);
    }

    @Test
    void getParticipantById_WhenExists_ShouldReturnParticipant() {
        // Arrange
        when(tripParticipantRepository.findById(1)).thenReturn(Optional.of(testParticipant));

        // Act
        Optional<TripParticipant> result = tripParticipantDAL.getParticipantById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testParticipant, result.get());
        assertEquals("ORGANIZER", result.get().getRole());
        verify(tripParticipantRepository, times(1)).findById(1);
    }

    @Test
    void getParticipantById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(tripParticipantRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<TripParticipant> result = tripParticipantDAL.getParticipantById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(tripParticipantRepository, times(1)).findById(999);
    }

    @Test
    void findByTripIdAndUserId_WhenExists_ShouldReturnParticipant() {
        // Arrange
        when(tripParticipantRepository.findByTripIdAndUserId(100, 1L)).thenReturn(Optional.of(testParticipant));

        // Act
        Optional<TripParticipant> result = tripParticipantDAL.findByTripIdAndUserId(100, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testParticipant, result.get());
        assertEquals(100, result.get().getTripId());
        assertEquals(1L, result.get().getUserId());
        verify(tripParticipantRepository, times(1)).findByTripIdAndUserId(100, 1L);
    }

    @Test
    void findByTripIdAndUserId_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(tripParticipantRepository.findByTripIdAndUserId(999, 999L)).thenReturn(Optional.empty());

        // Act
        Optional<TripParticipant> result = tripParticipantDAL.findByTripIdAndUserId(999, 999L);

        // Assert
        assertFalse(result.isPresent());
        verify(tripParticipantRepository, times(1)).findByTripIdAndUserId(999, 999L);
    }

    @Test
    void existsByTripIdAndUserId_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(tripParticipantRepository.existsByTripIdAndUserId(100, 1L)).thenReturn(true);

        // Act
        boolean result = tripParticipantDAL.existsByTripIdAndUserId(100, 1L);

        // Assert
        assertTrue(result);
        verify(tripParticipantRepository, times(1)).existsByTripIdAndUserId(100, 1L);
    }

    @Test
    void existsByTripIdAndUserId_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(tripParticipantRepository.existsByTripIdAndUserId(999, 999L)).thenReturn(false);

        // Act
        boolean result = tripParticipantDAL.existsByTripIdAndUserId(999, 999L);

        // Assert
        assertFalse(result);
        verify(tripParticipantRepository, times(1)).existsByTripIdAndUserId(999, 999L);
    }

    @Test
    void deleteParticipant_ShouldDeleteParticipant() {
        // Arrange
        doNothing().when(tripParticipantRepository).deleteById(1);

        // Act
        tripParticipantDAL.deleteParticipant(1);

        // Assert
        verify(tripParticipantRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteParticipant_WithNonExistentId_ShouldStillInvokeDelete() {
        // Arrange
        doNothing().when(tripParticipantRepository).deleteById(999);

        // Act
        tripParticipantDAL.deleteParticipant(999);

        // Assert
        verify(tripParticipantRepository, times(1)).deleteById(999);
    }

    @Test
    void deleteParticipantsByTripId_ShouldDeleteAllParticipantsForTrip() {
        // Arrange
        doNothing().when(tripParticipantRepository).deleteByTripId(100);

        // Act
        tripParticipantDAL.deleteParticipantsByTripId(100);

        // Assert
        verify(tripParticipantRepository, times(1)).deleteByTripId(100);
    }

    @Test
    void deleteParticipantsByTripId_WithNonExistentTrip_ShouldStillInvokeDelete() {
        // Arrange
        doNothing().when(tripParticipantRepository).deleteByTripId(999);

        // Act
        tripParticipantDAL.deleteParticipantsByTripId(999);

        // Assert
        verify(tripParticipantRepository, times(1)).deleteByTripId(999);
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(tripParticipantRepository.existsById(1)).thenReturn(true);

        // Act
        boolean result = tripParticipantDAL.existsById(1);

        // Assert
        assertTrue(result);
        verify(tripParticipantRepository, times(1)).existsById(1);
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(tripParticipantRepository.existsById(999)).thenReturn(false);

        // Act
        boolean result = tripParticipantDAL.existsById(999);

        // Assert
        assertFalse(result);
        verify(tripParticipantRepository, times(1)).existsById(999);
    }
}

