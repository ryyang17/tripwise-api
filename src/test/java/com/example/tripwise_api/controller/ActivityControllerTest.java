package com.example.tripwise_api.controller;

import com.example.tripwise_api.model.Activity;
import com.example.tripwise_api.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private ActivityController activityController;

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
    void getActivitiesByTrip_ShouldReturnActivities() {
        // Arrange
        List<Activity> activities = List.of(testActivity);
        when(activityService.getActivitiesSortedByDate(1L)).thenReturn(activities);

        // Act
        ResponseEntity<List<Activity>> response = activityController.getActivitiesByTrip(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activities, response.getBody());
        verify(activityService, times(1)).getActivitiesSortedByDate(1L);
    }

    @Test
    void getActivitiesByDate_ShouldReturnActivitiesGroupedByDate() {
        // Arrange
        Map<LocalDate, List<Activity>> activitiesByDate = Map.of(
            LocalDate.of(2025, 12, 2), List.of(testActivity)
        );
        when(activityService.getActivitiesByDate(1L)).thenReturn(activitiesByDate);

        // Act
        ResponseEntity<Map<LocalDate, List<Activity>>> response = activityController.getActivitiesByDate(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activitiesByDate, response.getBody());
        verify(activityService, times(1)).getActivitiesByDate(1L);
    }

    @Test
    void getActivityById_WhenExists_ShouldReturnActivity() {
        // Arrange
        when(activityService.getActivityById(1L)).thenReturn(Optional.of(testActivity));

        // Act
        ResponseEntity<Activity> response = activityController.getActivityById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testActivity, response.getBody());
        verify(activityService, times(1)).getActivityById(1L);
    }

    @Test
    void getActivityById_WhenNotExists_ShouldReturnNotFound() {
        // Arrange
        when(activityService.getActivityById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Activity> response = activityController.getActivityById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(activityService, times(1)).getActivityById(999L);
    }

    @Test
    void addActivityToTrip_WhenValid_ShouldReturnCreated() {
        // Arrange
        when(activityService.createActivity(eq(1L), any(Activity.class))).thenReturn(testActivity);

        // Act
        ResponseEntity<Activity> response = activityController.addActivityToTrip(1L, testActivity);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testActivity, response.getBody());
        verify(activityService, times(1)).createActivity(eq(1L), any(Activity.class));
    }

    @Test
    void addActivityToTrip_WhenInvalid_ShouldReturnBadRequest() {
        // Arrange
        when(activityService.createActivity(eq(1L), any(Activity.class)))
            .thenThrow(new IllegalArgumentException("Invalid activity"));

        // Act
        ResponseEntity<Activity> response = activityController.addActivityToTrip(1L, testActivity);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(activityService, times(1)).createActivity(eq(1L), any(Activity.class));
    }

    @Test
    void updateActivity_WhenValid_ShouldReturnUpdatedActivity() {
        // Arrange
        Activity updatedActivity = new Activity();
        updatedActivity.setName("Updated Activity");
        when(activityService.updateActivity(eq(1L), any(Activity.class))).thenReturn(updatedActivity);

        // Act
        ResponseEntity<Activity> response = activityController.updateActivity(1L, updatedActivity);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedActivity, response.getBody());
        verify(activityService, times(1)).updateActivity(eq(1L), any(Activity.class));
    }

    @Test
    void updateActivity_WhenNotFound_ShouldReturnNotFound() {
        // Arrange
        when(activityService.updateActivity(eq(999L), any(Activity.class)))
            .thenThrow(new IllegalArgumentException("Activity not found"));

        // Act
        ResponseEntity<Activity> response = activityController.updateActivity(999L, testActivity);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(activityService, times(1)).updateActivity(eq(999L), any(Activity.class));
    }

    @Test
    void deleteActivity_WhenValid_ShouldReturnSuccess() {
        // Arrange
        doNothing().when(activityService).deleteActivity(1L);

        // Act
        ResponseEntity<String> response = activityController.deleteActivity(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Activity deleted successfully", response.getBody());
        verify(activityService, times(1)).deleteActivity(1L);
    }

    @Test
    void deleteActivity_WhenNotFound_ShouldReturnNotFound() {
        // Arrange
        doThrow(new IllegalArgumentException("Activity not found"))
            .when(activityService).deleteActivity(999L);

        // Act
        ResponseEntity<String> response = activityController.deleteActivity(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Activity not found", response.getBody());
        verify(activityService, times(1)).deleteActivity(999L);
    }

    @Test
    void getActivitiesWithCost_ShouldReturnActivitiesWithCost() {
        // Arrange
        List<Activity> activities = List.of(testActivity);
        when(activityService.getActivitiesWithCost(1L)).thenReturn(activities);

        // Act
        ResponseEntity<List<Activity>> response = activityController.getActivitiesWithCost(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activities, response.getBody());
        verify(activityService, times(1)).getActivitiesWithCost(1L);
    }
}

