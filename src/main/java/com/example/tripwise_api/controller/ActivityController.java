package com.example.tripwise_api.controller;

import com.example.tripwise_api.model.Activity;
import com.example.tripwise_api.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    // GET /api/trips/{tripId}/activities - All activities for a trip (now sorted by date)
    @GetMapping("/trips/{tripId}/activities")
    public ResponseEntity<List<Activity>> getActivitiesByTrip(@PathVariable Long tripId) {
        List<Activity> activities = activityService.getActivitiesSortedByDate(tripId);
        return ResponseEntity.ok(activities);
    }

    // GET /api/trips/{tripId}/activities/by-date - Activities grouped by date
    @GetMapping("/trips/{tripId}/activities/by-date")
    public ResponseEntity<Map<LocalDate, List<Activity>>> getActivitiesByDate(@PathVariable Long tripId) {
        Map<LocalDate, List<Activity>> activitiesByDate = activityService.getActivitiesByDate(tripId);
        return ResponseEntity.ok(activitiesByDate);
    }

    // GET /api/activities/{id} - Specifieke activiteit
    @GetMapping("/activities/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        Optional<Activity> activity = activityService.getActivityById(id);
        return activity.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/trips/{tripId}/activities - Add new activity to trip (now with date validation)
    @PostMapping("/trips/{tripId}/activities")
    public ResponseEntity<Activity> addActivityToTrip(@PathVariable Long tripId, @RequestBody Activity activity) {
        try {
            Activity savedActivity = activityService.createActivity(tripId, activity);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedActivity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/activities/{id} - Update activity (now with date validation)
    @PutMapping("/activities/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activityDetails) {
        try {
            Activity updatedActivity = activityService.updateActivity(id, activityDetails);
            return ResponseEntity.ok(updatedActivity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE /api/activities/{id} - Verwijder activiteit
    @DeleteMapping("/activities/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
        try {
            activityService.deleteActivity(id);
            return ResponseEntity.ok("Activity deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Extra endpoint: GET /api/trips/{tripId}/activities/with-cost
    @GetMapping("/trips/{tripId}/activities/with-cost")
    public ResponseEntity<List<Activity>> getActivitiesWithCost(@PathVariable Long tripId) {
        List<Activity> activities = activityService.getActivitiesWithCost(tripId);
        return ResponseEntity.ok(activities);
    }
}