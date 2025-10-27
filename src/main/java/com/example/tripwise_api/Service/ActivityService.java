package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Activity;
import com.example.tripwise_api.Model.Trip;
import com.example.tripwise_api.DAL.ActivityDAL;
import com.example.tripwise_api.DAL.TripDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityService {

    @Autowired
    private ActivityDAL activityDAL;

    @Autowired
    private TripDAL tripDAL;

    @Transactional(readOnly = true)
    public List<Activity> getAllActivities() {
        return activityDAL.getAllActivities();
    }

    @Transactional(readOnly = true)
    public List<Activity> getActivitiesByTripId(Long tripId) {
        return activityDAL.getActivitiesByTripId(tripId);
    }

    @Transactional(readOnly = true)
    public Optional<Activity> getActivityById(Long id) {
        return activityDAL.getActivityById(id);
    }

    @Transactional(readOnly = true)
    public List<Activity> getActivitiesWithCost(Long tripId) {
        return activityDAL.getActivitiesWithCost(tripId);
    }

    // NEW: Get activities sorted by date and time
    @Transactional(readOnly = true)
    public List<Activity> getActivitiesSortedByDate(Long tripId) {
        return activityDAL.getActivitiesSortedByDate(tripId);
    }

    // NEW: Get activities grouped by date
    @Transactional(readOnly = true)
    public Map<LocalDate, List<Activity>> getActivitiesByDate(Long tripId) {
        List<Activity> activities = activityDAL.getActivitiesSortedByDate(tripId);
        return activities.stream()
                .filter(activity -> activity.getDate() != null)
                .collect(Collectors.groupingBy(Activity::getDate));
    }

    // Updated create method with date validation
    public Activity createActivity(Long tripId, Activity activity) {
        // 1. Get the trip to validate dates
        Trip trip = tripDAL.getTripById(tripId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with id: " + tripId));

        // 2. Simple date validation - only if date is provided
        if (activity.getDate() != null) {
            validateActivityDate(activity.getDate(), trip.getStartDate(), trip.getEndDate());
        }

        // 3. Set trip ID and create activity
        activity.setTripId(tripId);
        return activityDAL.saveActivity(activity);
    }

    // Updated update method to include date validation
    public Activity updateActivity(Long id, Activity activityDetails) {
        Activity activity = activityDAL.getActivityById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found with id: " + id));

        // Validate date if being updated
        if (activityDetails.getDate() != null) {
            Trip trip = tripDAL.getTripById(activity.getTripId().intValue())
                    .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
            validateActivityDate(activityDetails.getDate(), trip.getStartDate(), trip.getEndDate());
        }

        activity.setName(activityDetails.getName());
        activity.setDescription(activityDetails.getDescription());
        activity.setDate(activityDetails.getDate()); // NEW: Update date
        activity.updateTime(activityDetails.getTime());
        activity.updateLocation(activityDetails.getLocation());
        activity.setEstimatedCost(activityDetails.getEstimatedCost());

        return activityDAL.saveActivity(activity);
    }

    public void deleteActivity(Long id) {
        if (!activityDAL.existsById(id)) {
            throw new IllegalArgumentException("Activity not found with id: " + id);
        }
        activityDAL.deleteActivity(id);
    }

    // Simple validation method
    private void validateActivityDate(LocalDate activityDate, LocalDate tripStart, LocalDate tripEnd) {
        if (tripStart != null && activityDate.isBefore(tripStart)) {
            throw new IllegalArgumentException(
                "Activity date must be on or after trip start date: " + tripStart
            );
        }
        if (tripEnd != null && activityDate.isAfter(tripEnd)) {
            throw new IllegalArgumentException(
                "Activity date must be on or before trip end date: " + tripEnd
            );
        }
    }
}
