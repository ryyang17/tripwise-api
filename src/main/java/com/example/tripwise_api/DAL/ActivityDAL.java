package com.example.tripwise_api.DAL;

import com.example.tripwise_api.Model.Activity;
import com.example.tripwise_api.Repository.ActivityRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ActivityDAL {
    private final ActivityRepository activityRepository;

    public ActivityDAL(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public List<Activity> getActivitiesByTripId(Long tripId) {
        return activityRepository.findByTripId(tripId);
    }

    public List<Activity> getActivitiesWithCost(Long tripId) {
        return activityRepository.findByTripIdAndEstimatedCostIsNotNull(tripId);
    }

    public List<Activity> getActivitiesSortedByDate(Long tripId) {
        return activityRepository.findByTripIdOrderByDateAscTimeAsc(tripId);
    }

    public List<Activity> getActivitiesByDate(Long tripId, LocalDate date) {
        return activityRepository.findByTripIdAndDate(tripId, date);
    }

    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return activityRepository.existsById(id);
    }
}
