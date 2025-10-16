package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Activity;
import com.example.tripwise_api.DAL.ActivityDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActivityService {

    @Autowired
    private ActivityDAL activityDAL;

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

    public Activity createActivity(Long tripId, Activity activity) {
        activity.setTripId(tripId);
        return activityDAL.saveActivity(activity);
    }

    public Activity updateActivity(Long id, Activity activityDetails) {
        Activity activity = activityDAL.getActivityById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found with id: " + id));

        activity.setName(activityDetails.getName());
        activity.setDescription(activityDetails.getDescription());
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
}
