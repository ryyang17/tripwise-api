package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Activity;
import com.example.tripwise_api.DAL.ActivityDAL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActivityService {
    private final ActivityDAL activityDAL;

    public ActivityService(ActivityDAL activityDAL) {
        this.activityDAL = activityDAL;
    }

    @Transactional(readOnly = true)
    public List<Activity> getAllActivities() {
        return activityDAL.getAllActivities();
    }

    @Transactional(readOnly = true)
    public Optional<Activity> getActivityById(Long id) {
        return activityDAL.getActivityById(id);
    }

    public Activity createActivity(Activity activity) {
        // Add business validation here if needed
        return activityDAL.saveActivity(activity);
    }

    public Activity updateActivity(Long id, Activity activityDetails) {
        Activity existingActivity = activityDAL.getActivityById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));
        // Update fields as needed
        // ...existing code...
        return activityDAL.saveActivity(existingActivity);
    }

    public void deleteActivity(Long id) {
        if (!activityDAL.existsById(id)) {
            throw new RuntimeException("Activity not found with id: " + id);
        }
        activityDAL.deleteActivity(id);
    }
}

