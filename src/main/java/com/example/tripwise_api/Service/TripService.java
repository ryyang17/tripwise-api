package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Trip;
import com.example.tripwise_api.DAL.TripDAL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * TripService: Business logic layer for Trip operations
 * This class uses TripDAL (Data Access Layer) to interact with the database.
 * The DAL abstracts all data access, so TripService focuses only on business rules and validation.
 */
@Service
@Transactional
public class TripService {
    // TripDAL is injected here. All database operations are delegated to DAL.
    private final TripDAL tripDAL;

    public TripService(TripDAL tripDAL) {
        this.tripDAL = tripDAL;
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
        return tripDAL.saveTrip(trip);
    }


    public Trip updateTrip(Integer id, Trip tripDetails) {
        Trip existingTrip = tripDAL.getTripById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

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
            throw new RuntimeException("Trip not found with id: " + id);
        }
        tripDAL.deleteTrip(id);
    }



    /**
     * Basic validation for trip data
     * This logic is part of the service layer, not DAL.
     */
    private void validateTrip(Trip trip) {
        if (trip.getTripName() == null || trip.getTripName().trim().isEmpty()) {
            throw new IllegalArgumentException("Trip name cannot be empty");
        }

        if (trip.getStartDate() != null && trip.getEndDate() != null &&
                trip.getStartDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (trip.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }
}