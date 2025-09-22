package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Trip;
import com.example.tripwise_api.Repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    // GET /api/trips - Alle trips
    @GetMapping
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    // GET /api/trips/{id} - Één trip ophalen
    @GetMapping("/{id}")
    public Optional<Trip> getTripById(@PathVariable Integer id) {
        return tripRepository.findById(id);
    }

    // GET /api/trips/user/{userId} - Alle trips van een specifieke gebruiker
    @GetMapping("/user/{userId}")
    public List<Trip> getTripsByUserId(@PathVariable Long userId) {
        return tripRepository.findByUserId(userId);
    }


    // POST /api/trips - Nieuwe trip aanmaken
    @PostMapping
    public Trip createTrip(@RequestBody Trip trip) {
        return tripRepository.save(trip);
    }

    // PUT /api/trips/{id} - Trip updaten
    @PutMapping("/{id}")
    public Trip updateTrip(@PathVariable Integer id, @RequestBody Trip tripDetails) {
        Trip trip = tripRepository.findById(id).orElseThrow();

        trip.setUserId(tripDetails.getUserId());
        trip.setTripName(tripDetails.getTripName());
        trip.setStartDate(tripDetails.getStartDate());
        trip.setEndDate(tripDetails.getEndDate());
        trip.setDestination(tripDetails.getDestination());
        trip.setDescription(tripDetails.getDescription());
        trip.setIsActive(tripDetails.getIsActive());

        return tripRepository.save(trip);
    }

    // DELETE /api/trips/{id} - Trip verwijderen
    @DeleteMapping("/{id}")
    public void deleteTrip(@PathVariable Integer id) {
        tripRepository.deleteById(id);
    }
}