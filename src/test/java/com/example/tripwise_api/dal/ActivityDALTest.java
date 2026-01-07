package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.Activity;
import com.example.tripwise_api.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityDALTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityDAL activityDAL;

    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testActivity = new Activity();
        testActivity.setActivityId(1L);
        testActivity.setTripId(1L);
        testActivity.setName("Test Activity");
        testActivity.setDescription("Test Description");
        testActivity.setDate(LocalDate.of(2025, 12, 2));
        testActivity.setTime(LocalTime.of(10, 0));
        testActivity.setLocation("Test Location");
        testActivity.setEstimatedCost(new BigDecimal("50.00"));
    }

    @Test
    void getAllActivities_ShouldReturnAllActivities() {
        // Arrange
        List<Activity> expectedActivities = List.of(testActivity);
        when(activityRepository.findAll()).thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityDAL.getAllActivities();

        // Assert
        assertEquals(expectedActivities, result);
        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void getActivitiesByTripId_ShouldReturnTripActivities() {
        // Arrange
        List<Activity> expectedActivities = List.of(testActivity);
        when(activityRepository.findByTripId(1L)).thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityDAL.getActivitiesByTripId(1L);

        // Assert
        assertEquals(expectedActivities, result);
        verify(activityRepository, times(1)).findByTripId(1L);
    }

    @Test
    void getActivitiesWithCost_ShouldReturnActivitiesWithCost() {
        // Arrange
        List<Activity> expectedActivities = List.of(testActivity);
        when(activityRepository.findByTripIdAndEstimatedCostIsNotNull(1L))
            .thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityDAL.getActivitiesWithCost(1L);

        // Assert
        assertEquals(expectedActivities, result);
        verify(activityRepository, times(1))
            .findByTripIdAndEstimatedCostIsNotNull(1L);
    }

    @Test
    void getActivitiesSortedByDate_ShouldReturnSortedActivities() {
        // Arrange
        List<Activity> expectedActivities = List.of(testActivity);
        when(activityRepository.findByTripIdOrderByDateAscTimeAsc(1L))
            .thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityDAL.getActivitiesSortedByDate(1L);

        // Assert
        assertEquals(expectedActivities, result);
        verify(activityRepository, times(1))
            .findByTripIdOrderByDateAscTimeAsc(1L);
    }

    @Test
    void getActivitiesByDate_ShouldReturnActivitiesForDate() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 12, 2);
        List<Activity> expectedActivities = List.of(testActivity);
        when(activityRepository.findByTripIdAndDate(1L, date))
            .thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityDAL.getActivitiesByDate(1L, date);

        // Assert
        assertEquals(expectedActivities, result);
        verify(activityRepository, times(1)).findByTripIdAndDate(1L, date);
    }

    @Test
    void getActivityById_WhenExists_ShouldReturnActivity() {
        // Arrange
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));

        // Act
        Optional<Activity> result = activityDAL.getActivityById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testActivity, result.get());
        verify(activityRepository, times(1)).findById(1L);
    }

    @Test
    void getActivityById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(activityRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Activity> result = activityDAL.getActivityById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(activityRepository, times(1)).findById(999L);
    }

    @Test
    void saveActivity_ShouldSaveAndReturnActivity() {
        // Arrange
        when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

        // Act
        Activity result = activityDAL.saveActivity(testActivity);

        // Assert
        assertNotNull(result);
        assertEquals(testActivity, result);
        verify(activityRepository, times(1)).save(testActivity);
    }

    @Test
    void deleteActivity_ShouldDeleteActivity() {
        // Arrange
        doNothing().when(activityRepository).deleteById(1L);

        // Act
        activityDAL.deleteActivity(1L);

        // Assert
        verify(activityRepository, times(1)).deleteById(1L);
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(activityRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = activityDAL.existsById(1L);

        // Assert
        assertTrue(result);
        verify(activityRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(activityRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = activityDAL.existsById(999L);

        // Assert
        assertFalse(result);
        verify(activityRepository, times(1)).existsById(999L);
    }
}

