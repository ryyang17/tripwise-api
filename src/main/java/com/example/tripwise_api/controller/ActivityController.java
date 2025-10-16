package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Activity;
import com.example.tripwise_api.Service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // GET /api/trips/{tripId}/activities - Alle activiteiten voor een trip
    @GetMapping("/trips/{tripId}/activities")
    public ResponseEntity<List<Activity>> getActivitiesByTrip(@PathVariable Long tripId) {
        List<Activity> activities = activityService.getActivitiesByTripId(tripId);
        return ResponseEntity.ok(activities);
    }

    // GET /api/activities/{id} - Specifieke activiteit
    @GetMapping("/activities/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        Optional<Activity> activity = activityService.getActivityById(id);
        return activity.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/trips/{tripId}/activities - Nieuwe activiteit toevoegen aan trip
    @PostMapping("/trips/{tripId}/activities")
    public ResponseEntity<?> addActivityToTrip(@PathVariable Long tripId, @RequestBody Activity activity) {
        try {
            Activity savedActivity = activityService.createActivity(tripId, activity);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedActivity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/activities/{id} - Update activiteit
    @PutMapping("/activities/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable Long id, @RequestBody Activity activityDetails) {
        try {
            Activity updatedActivity = activityService.updateActivity(id, activityDetails);
            return ResponseEntity.ok(updatedActivity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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