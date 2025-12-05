package com.example.tripwise_api.service;

import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.model.TripParticipant;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.dal.TripDAL;
import com.example.tripwise_api.service.exception.TripNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripService {
    private final TripDAL tripDAL;
    private final TripParticipantService tripParticipantService;
    private final UserService userService;

    public TripService(TripDAL tripDAL, TripParticipantService tripParticipantService, UserService userService) {
        this.tripDAL = tripDAL;
        this.tripParticipantService = tripParticipantService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Trip> getAllTrips() {
        // Example of using DAL for data access
        return tripDAL.getAllTrips();
    }


    @Transactional(readOnly = true)
    public Optional<Trip> getTripById(Integer id) {
        // All queries go through DAL
        return tripDAL.getTripById(id);
    }


    @Transactional(readOnly = true)
    public List<Trip> getTripsByUserId(Long userId) {
        return tripDAL.getTripsByUserId(userId);
    }


    public Trip createTrip(Trip trip) {
        validateTrip(trip);
        // Save operation is handled by DAL
        Trip savedTrip = tripDAL.saveTrip(trip);

        // Automatically add the trip creator as a participant with "owner" role
        Optional<User> userOpt = userService.getUserById(trip.getUserId());
        if (userOpt.isPresent()) {
            TripParticipant participant = new TripParticipant();
            participant.setTripId(savedTrip.getTripId());
            participant.setUser(userOpt.get()); // Set the User entity instead of just userId
            participant.setRole("owner"); // Set as owner since they created the trip
            tripParticipantService.addParticipant(participant);
        } else {
            throw new IllegalArgumentException("User not found with ID: " + trip.getUserId());
        }

        return savedTrip;
    }


    public Trip updateTrip(Integer id, Trip tripDetails) {
        Trip existingTrip = tripDAL.getTripById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + id));

        validateTrip(tripDetails);

        // Update fields
        existingTrip.setUserId(tripDetails.getUserId());
        existingTrip.setTripName(tripDetails.getTripName());
        existingTrip.setStartDate(tripDetails.getStartDate());
        existingTrip.setEndDate(tripDetails.getEndDate());
        existingTrip.setDestination(tripDetails.getDestination());
        existingTrip.setDescription(tripDetails.getDescription());
        existingTrip.setTransportation(tripDetails.getTransportation());


        return tripDAL.saveTrip(existingTrip);
    }


    public void deleteTrip(Integer id) {
        if (!tripDAL.existsById(id)) {
            throw new TripNotFoundException("Trip not found with id: " + id);
        }

        // First remove all participants referencing this trip to avoid FK constraint violations
        tripParticipantService.deleteParticipantsByTripId(id);

        // Then delete the trip itself
        tripDAL.deleteTrip(id);
    }

    // Simple validation method
    private void validateTrip(Trip trip) {
        if (trip.getTripName() == null || trip.getTripName().trim().isEmpty()) {
            throw new IllegalArgumentException("Trip name cannot be empty");
        }

        if (trip.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (trip.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }

        if (trip.getStartDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (trip.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }
}