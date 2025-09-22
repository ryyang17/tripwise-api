package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Activity;
import com.example.tripwise_api.Repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    // GET /api/trips/{tripId}/activities - Alle activiteiten voor een trip
    @GetMapping("/trips/{tripId}/activities")
    public List<Activity> getActivitiesByTrip(@PathVariable Long tripId) {
        return activityRepository.findByTripId(tripId);
    }

    // GET /api/activities/{id} - Specifieke activiteit
    @GetMapping("/activities/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isPresent()) {
            return ResponseEntity.ok(activity.get());
        }
        return ResponseEntity.notFound().build();
    }

    // POST /api/trips/{tripId}/activities - Nieuwe activiteit toevoegen aan trip
    @PostMapping("/trips/{tripId}/activities")
    public Activity addActivityToTrip(@PathVariable Long tripId, @RequestBody Activity activity) {
        activity.setTripId(tripId);  // Zorg ervoor dat tripId correct is
        return activityRepository.save(activity);
    }

    // PUT /api/activities/{id} - Update activiteit
    @PutMapping("/activities/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activityDetails) {
        Optional<Activity> optionalActivity = activityRepository.findById(id);

        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();

            // Update velden
            activity.setName(activityDetails.getName());
            activity.setDescription(activityDetails.getDescription());
            activity.updateTime(activityDetails.getTime());
            activity.updateLocation(activityDetails.getLocation());
            activity.setEstimatedCost(activityDetails.getEstimatedCost());

            Activity updatedActivity = activityRepository.save(activity);
            return ResponseEntity.ok(updatedActivity);
        }

        return ResponseEntity.notFound().build();
    }

    // DELETE /api/activities/{id} - Verwijder activiteit
    @DeleteMapping("/activities/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
        if (activityRepository.existsById(id)) {
            activityRepository.deleteById(id);
            return ResponseEntity.ok("Activity deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    // Extra endpoint: GET /api/trips/{tripId}/activities/with-cost
    @GetMapping("/trips/{tripId}/activities/with-cost")
    public List<Activity> getActivitiesWithCost(@PathVariable Long tripId) {
        return activityRepository.findByTripIdAndEstimatedCostIsNotNull(tripId);
    }
}