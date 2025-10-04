package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Trip;
import com.example.tripwise_api.Service.TripService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // GET /api/trips - Alle trips
    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    // GET /api/trips/{id} - Één trip ophalen
    @GetMapping("/{id}")
    public Optional<Trip> getTripById(@PathVariable Integer id) {
        return tripService.getTripById(id);
    }

    // GET /api/trips/user/{userId} - Alle trips van een specifieke gebruiker
    @GetMapping("/user/{userId}")
    public List<Trip> getTripsByUserId(@PathVariable Long userId) {
        return tripService.getTripsByUserId(userId);
    }


    // POST /api/trips - Nieuwe trip aanmaken
    @PostMapping
    public Trip createTrip(@RequestBody Trip trip) {
        return tripService.createTrip(trip);
    }

    // PUT /api/trips/{id} - Trip updaten
    @PutMapping("/{id}")
    public Trip updateTrip(@PathVariable Integer id, @RequestBody Trip tripDetails) {
        return tripService.updateTrip(id, tripDetails);
    }

    // DELETE /api/trips/{id} - Trip verwijderen
    @DeleteMapping("/{id}")
    public void deleteTrip(@PathVariable Integer id) {
        tripService.deleteTrip(id);
    }
}