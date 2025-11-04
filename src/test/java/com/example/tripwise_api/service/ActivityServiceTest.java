package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.ActivityDAL;
import com.example.tripwise_api.dal.TripDAL;
import com.example.tripwise_api.model.Activity;
import com.example.tripwise_api.model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityDAL activityDAL;

    @Mock
    private TripDAL tripDAL;

    @InjectMocks
    private ActivityService activityService;

    private Activity validActivity;
    private Trip validTrip;

    @BeforeEach
    void setUp() {
        validTrip = new Trip();
        validTrip.setTripId(1);
        validTrip.setStartDate(LocalDate.of(2025, 12, 1));
        validTrip.setEndDate(LocalDate.of(2025, 12, 5));

        validActivity = new Activity();
        validActivity.setActivityId(1L);
        validActivity.setTripId(1L);
        validActivity.setName("Test Activity");
        validActivity.setDescription("Test Description");
        validActivity.setDate(LocalDate.of(2025, 12, 2));
        validActivity.setTime(LocalTime.of(10, 0));
        validActivity.setLocation("Test Location");
        validActivity.setEstimatedCost(new BigDecimal("50.00"));
    }

    @Test
    void getAllActivities_ShouldReturnAllActivities() {
        // Arrange
        List<Activity> expectedActivities = List.of(validActivity);
        when(activityDAL.getAllActivities()).thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityService.getAllActivities();

        // Assert
        assertEquals(expectedActivities, result);
        verify(activityDAL, times(1)).getAllActivities();
    }

    @Test
    void getActivitiesByTripId_ShouldReturnTripActivities() {
        // Arrange
        List<Activity> expectedActivities = List.of(validActivity);
        when(activityDAL.getActivitiesByTripId(1L)).thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityService.getActivitiesByTripId(1L);

        // Assert
        assertEquals(expectedActivities, result);
        verify(activityDAL, times(1)).getActivitiesByTripId(1L);
    }

    @Test
    void getActivitiesByTripId_WithNoActivities_ShouldReturnEmptyList() {
        // Arrange
        when(activityDAL.getActivitiesByTripId(999L)).thenReturn(Collections.emptyList());

        // Act
        List<Activity> result = activityService.getActivitiesByTripId(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(activityDAL, times(1)).getActivitiesByTripId(999L);
    }

    @Test
    void getActivityById_WhenActivityExists_ShouldReturnActivity() {
        // Arrange
        when(activityDAL.getActivityById(1L)).thenReturn(Optional.of(validActivity));

        // Act
        Optional<Activity> result = activityService.getActivityById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validActivity, result.get());
        verify(activityDAL, times(1)).getActivityById(1L);
    }

    @Test
    void getActivityById_WhenActivityDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(activityDAL.getActivityById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Activity> result = activityService.getActivityById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(activityDAL, times(1)).getActivityById(999L);
    }

    @Test
    void getActivitiesWithCost_ShouldReturnActivitiesWithCost() {
        // Arrange
        List<Activity> expectedActivities = List.of(validActivity);
        when(activityDAL.getActivitiesWithCost(1L)).thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityService.getActivitiesWithCost(1L);

        // Assert
        assertEquals(expectedActivities, result);
        verify(activityDAL, times(1)).getActivitiesWithCost(1L);
    }

    @Test
    void getActivitiesSortedByDate_ShouldReturnSortedActivities() {
        // Arrange
        List<Activity> expectedActivities = List.of(validActivity);
        when(activityDAL.getActivitiesSortedByDate(1L)).thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityService.getActivitiesSortedByDate(1L);

        // Assert
        assertEquals(expectedActivities, result);
        verify(activityDAL, times(1)).getActivitiesSortedByDate(1L);
    }

    @Test
    void getActivitiesByDate_ShouldReturnActivitiesGroupedByDate() {
        // Arrange
        Activity activity1 = new Activity();
        activity1.setActivityId(1L);
        activity1.setDate(LocalDate.of(2025, 12, 2));
        activity1.setName("Morning Activity");

        Activity activity2 = new Activity();
        activity2.setActivityId(2L);
        activity2.setDate(LocalDate.of(2025, 12, 2));
        activity2.setName("Evening Activity");

        Activity activity3 = new Activity();
        activity3.setActivityId(3L);
        activity3.setDate(LocalDate.of(2025, 12, 3));
        activity3.setName("Next Day Activity");

        List<Activity> activities = List.of(activity1, activity2, activity3);
        when(activityDAL.getActivitiesSortedByDate(1L)).thenReturn(activities);

        // Act
        Map<LocalDate, List<Activity>> result = activityService.getActivitiesByDate(1L);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(LocalDate.of(2025, 12, 2)));
        assertTrue(result.containsKey(LocalDate.of(2025, 12, 3)));
        assertEquals(2, result.get(LocalDate.of(2025, 12, 2)).size());
        assertEquals(1, result.get(LocalDate.of(2025, 12, 3)).size());
        verify(activityDAL, times(1)).getActivitiesSortedByDate(1L);
    }

    @Test
    void getActivitiesByDate_WithActivitiesWithoutDate_ShouldExcludeNullDates() {
        // Arrange
        Activity activityWithDate = new Activity();
        activityWithDate.setActivityId(1L);
        activityWithDate.setDate(LocalDate.of(2025, 12, 2));

        Activity activityWithoutDate = new Activity();
        activityWithoutDate.setActivityId(2L);
        activityWithoutDate.setDate(null);

        List<Activity> activities = List.of(activityWithDate, activityWithoutDate);
        when(activityDAL.getActivitiesSortedByDate(1L)).thenReturn(activities);

        // Act
        Map<LocalDate, List<Activity>> result = activityService.getActivitiesByDate(1L);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.containsKey(LocalDate.of(2025, 12, 2)));
        assertEquals(1, result.get(LocalDate.of(2025, 12, 2)).size());
        verify(activityDAL, times(1)).getActivitiesSortedByDate(1L);
    }

    @Test
    void createActivity_WithValidActivity_ShouldCreateActivity() {
        // Arrange
        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));
        when(activityDAL.saveActivity(any(Activity.class))).thenReturn(validActivity);

        // Act
        Activity result = activityService.createActivity(1L, validActivity);

        // Assert
        assertEquals(validActivity, result);
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, times(1)).saveActivity(validActivity);
    }

    @Test
    void createActivity_WithNonExistentTrip_ShouldThrowException() {
        // Arrange
        when(tripDAL.getTripById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> activityService.createActivity(999L, validActivity)
        );
        assertEquals("Trip not found with id: 999", exception.getMessage());
        verify(tripDAL, times(1)).getTripById(999);
        verify(activityDAL, never()).saveActivity(any(Activity.class));
    }

    @Test
    void createActivity_WithActivityDateBeforeTripStart_ShouldThrowException() {
        // Arrange
        Activity activityWithEarlyDate = new Activity();
        activityWithEarlyDate.setDate(LocalDate.of(2025, 11, 30)); // Before trip start
        activityWithEarlyDate.setName("Early Activity");

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> activityService.createActivity(1L, activityWithEarlyDate)
        );
        assertEquals("Activity date must be on or after trip start date: 2025-12-01", exception.getMessage());
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, never()).saveActivity(any(Activity.class));
    }

    @Test
    void createActivity_WithActivityDateAfterTripEnd_ShouldThrowException() {
        // Arrange
        Activity activityWithLateDate = new Activity();
        activityWithLateDate.setDate(LocalDate.of(2025, 12, 10)); // After trip end
        activityWithLateDate.setName("Late Activity");

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> activityService.createActivity(1L, activityWithLateDate)
        );
        assertEquals("Activity date must be on or before trip end date: 2025-12-05", exception.getMessage());
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, never()).saveActivity(any(Activity.class));
    }

    @Test
    void createActivity_WithActivityDateOnTripStart_ShouldSucceed() {
        // Arrange
        Activity activityOnStartDate = new Activity();
        activityOnStartDate.setDate(LocalDate.of(2025, 12, 1)); // On trip start
        activityOnStartDate.setName("Start Day Activity");

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));
        when(activityDAL.saveActivity(any(Activity.class))).thenReturn(activityOnStartDate);

        // Act
        Activity result = activityService.createActivity(1L, activityOnStartDate);

        // Assert
        assertNotNull(result);
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, times(1)).saveActivity(activityOnStartDate);
    }

    @Test
    void createActivity_WithActivityDateOnTripEnd_ShouldSucceed() {
        // Arrange
        Activity activityOnEndDate = new Activity();
        activityOnEndDate.setDate(LocalDate.of(2025, 12, 5)); // On trip end
        activityOnEndDate.setName("End Day Activity");

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));
        when(activityDAL.saveActivity(any(Activity.class))).thenReturn(activityOnEndDate);

        // Act
        Activity result = activityService.createActivity(1L, activityOnEndDate);

        // Assert
        assertNotNull(result);
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, times(1)).saveActivity(activityOnEndDate);
    }

    @Test
    void createActivity_WithNullActivityDate_ShouldSucceed() {
        // Arrange
        Activity activityWithoutDate = new Activity();
        activityWithoutDate.setDate(null);
        activityWithoutDate.setName("Flexible Activity");

        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));
        when(activityDAL.saveActivity(any(Activity.class))).thenReturn(activityWithoutDate);

        // Act
        Activity result = activityService.createActivity(1L, activityWithoutDate);

        // Assert
        assertNotNull(result);
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, times(1)).saveActivity(activityWithoutDate);
    }

    @Test
    void updateActivity_WhenActivityExists_ShouldUpdateActivity() {
        // Arrange
        Activity existingActivity = new Activity();
        existingActivity.setActivityId(1L);
        existingActivity.setTripId(1L);
        existingActivity.setName("Old Name");

        Activity updateDetails = new Activity();
        updateDetails.setName("New Name");
        updateDetails.setDescription("New Description");
        updateDetails.setDate(LocalDate.of(2025, 12, 3));
        updateDetails.setTime(LocalTime.of(14, 0));
        updateDetails.setLocation("New Location");
        updateDetails.setEstimatedCost(new BigDecimal("75.00"));

        when(activityDAL.getActivityById(1L)).thenReturn(Optional.of(existingActivity));
        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));
        when(activityDAL.saveActivity(any(Activity.class))).thenReturn(existingActivity);

        // Act
        Activity result = activityService.updateActivity(1L, updateDetails);

        // Assert
        assertNotNull(result);
        verify(activityDAL, times(1)).getActivityById(1L);
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, times(1)).saveActivity(existingActivity);
        assertEquals("New Name", existingActivity.getName());
    }

    @Test
    void updateActivity_WhenActivityDoesNotExist_ShouldThrowException() {
        // Arrange
        when(activityDAL.getActivityById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> activityService.updateActivity(999L, validActivity)
        );
        assertEquals("Activity not found with id: 999", exception.getMessage());
        verify(activityDAL, times(1)).getActivityById(999L);
        verify(activityDAL, never()).saveActivity(any(Activity.class));
    }

    @Test
    void updateActivity_WithInvalidDate_ShouldThrowException() {
        // Arrange
        Activity existingActivity = new Activity();
        existingActivity.setActivityId(1L);
        existingActivity.setTripId(1L);

        Activity updateWithInvalidDate = new Activity();
        updateWithInvalidDate.setDate(LocalDate.of(2025, 11, 30)); // Before trip start

        when(activityDAL.getActivityById(1L)).thenReturn(Optional.of(existingActivity));
        when(tripDAL.getTripById(1)).thenReturn(Optional.of(validTrip));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> activityService.updateActivity(1L, updateWithInvalidDate)
        );
        assertEquals("Activity date must be on or after trip start date: 2025-12-01", exception.getMessage());
        verify(activityDAL, times(1)).getActivityById(1L);
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, never()).saveActivity(any(Activity.class));
    }

    @Test
    void deleteActivity_WhenActivityExists_ShouldDeleteActivity() {
        // Arrange
        when(activityDAL.existsById(1L)).thenReturn(true);

        // Act
        activityService.deleteActivity(1L);

        // Assert
        verify(activityDAL, times(1)).existsById(1L);
        verify(activityDAL, times(1)).deleteActivity(1L);
    }

    @Test
    void deleteActivity_WhenActivityDoesNotExist_ShouldThrowException() {
        // Arrange
        when(activityDAL.existsById(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> activityService.deleteActivity(999L)
        );
        assertEquals("Activity not found with id: 999", exception.getMessage());
        verify(activityDAL, times(1)).existsById(999L);
        verify(activityDAL, never()).deleteActivity(999L);
    }

    @Test
    void updateActivity_WithNullDate_ShouldSucceed() {
        // Arrange
        Activity existingActivity = new Activity();
        existingActivity.setActivityId(1L);
        existingActivity.setTripId(1L);
        existingActivity.setName("Old Name");

        Activity updateWithNullDate = new Activity();
        updateWithNullDate.setName("Updated Name");
        updateWithNullDate.setDescription("Updated Description");
        updateWithNullDate.setDate(null); // Null date should not trigger validation
        updateWithNullDate.setTime(LocalTime.of(16, 0));
        updateWithNullDate.setLocation("Updated Location");
        updateWithNullDate.setEstimatedCost(new BigDecimal("100.00"));

        when(activityDAL.getActivityById(1L)).thenReturn(Optional.of(existingActivity));
        when(activityDAL.saveActivity(any(Activity.class))).thenReturn(existingActivity);

        // Act
        Activity result = activityService.updateActivity(1L, updateWithNullDate);

        // Assert
        assertNotNull(result);
        verify(activityDAL, times(1)).getActivityById(1L);
        verify(tripDAL, never()).getTripById(anyInt()); // Should not validate trip when date is null
        verify(activityDAL, times(1)).saveActivity(existingActivity);
        assertEquals("Updated Name", existingActivity.getName());
        assertEquals("Updated Description", existingActivity.getDescription());
        assertNull(existingActivity.getDate());
    }

    @Test
    void updateActivity_WhenTripNotFound_ShouldThrowException() {
        // Arrange
        Activity existingActivity = new Activity();
        existingActivity.setActivityId(1L);
        existingActivity.setTripId(999L); // Changed from 999 to 999L

        Activity updateWithDate = new Activity();
        updateWithDate.setDate(LocalDate.of(2025, 12, 3));

        when(activityDAL.getActivityById(1L)).thenReturn(Optional.of(existingActivity));
        when(tripDAL.getTripById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> activityService.updateActivity(1L, updateWithDate)
        );
        assertEquals("Trip not found", exception.getMessage());
        verify(activityDAL, times(1)).getActivityById(1L);
        verify(tripDAL, times(1)).getTripById(999);
        verify(activityDAL, never()).saveActivity(any(Activity.class));
    }

    @Test
    void updateActivity_WhenTripNotFoundDuringValidation_ShouldThrowException() {
        // Arrange
        Activity existingActivity = new Activity();
        existingActivity.setActivityId(1L);
        existingActivity.setTripId(999L);
        existingActivity.setName("Existing Activity");

        Activity updateWithDate = new Activity();
        updateWithDate.setName("Updated Name");
        updateWithDate.setDate(LocalDate.of(2025, 12, 3)); // This will trigger date validation

        when(activityDAL.getActivityById(1L)).thenReturn(Optional.of(existingActivity));
        when(tripDAL.getTripById(999)).thenReturn(Optional.empty()); // Trip not found

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> activityService.updateActivity(1L, updateWithDate)
        );
        assertEquals("Trip not found", exception.getMessage());
        verify(activityDAL, times(1)).getActivityById(1L);
        verify(tripDAL, times(1)).getTripById(999);
        verify(activityDAL, never()).saveActivity(any(Activity.class));
    }

    @Test
    void validateActivityDate_WithNullTripStartAndValidEndDate_ShouldSucceed() {
        // Arrange
        Trip tripWithNullStart = new Trip();
        tripWithNullStart.setTripId(1);
        tripWithNullStart.setStartDate(null);
        tripWithNullStart.setEndDate(LocalDate.of(2025, 12, 5));

        Activity existingActivity = new Activity();
        existingActivity.setActivityId(1L);
        existingActivity.setTripId(1L);

        Activity activityWithValidDate = new Activity();
        activityWithValidDate.setDate(LocalDate.of(2025, 12, 3)); // Within end date

        when(activityDAL.getActivityById(1L)).thenReturn(Optional.of(existingActivity));
        when(tripDAL.getTripById(1)).thenReturn(Optional.of(tripWithNullStart));
        when(activityDAL.saveActivity(any(Activity.class))).thenReturn(existingActivity);

        // Act
        Activity result = activityService.updateActivity(1L, activityWithValidDate);

        // Assert
        assertNotNull(result);
        verify(activityDAL, times(1)).getActivityById(1L);
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, times(1)).saveActivity(existingActivity);
    }

    @Test
    void validateActivityDate_WithValidStartDateAndNullTripEnd_ShouldSucceed() {
        // Arrange
        Trip tripWithNullEnd = new Trip();
        tripWithNullEnd.setTripId(1);
        tripWithNullEnd.setStartDate(LocalDate.of(2025, 12, 1));
        tripWithNullEnd.setEndDate(null);

        Activity existingActivity = new Activity();
        existingActivity.setActivityId(1L);
        existingActivity.setTripId(1L);

        Activity activityWithValidDate = new Activity();
        activityWithValidDate.setDate(LocalDate.of(2025, 12, 3)); // After start date

        when(activityDAL.getActivityById(1L)).thenReturn(Optional.of(existingActivity));
        when(tripDAL.getTripById(1)).thenReturn(Optional.of(tripWithNullEnd));
        when(activityDAL.saveActivity(any(Activity.class))).thenReturn(existingActivity);

        // Act
        Activity result = activityService.updateActivity(1L, activityWithValidDate);

        // Assert
        assertNotNull(result);
        verify(activityDAL, times(1)).getActivityById(1L);
        verify(tripDAL, times(1)).getTripById(1);
        verify(activityDAL, times(1)).saveActivity(existingActivity);
    }
}
