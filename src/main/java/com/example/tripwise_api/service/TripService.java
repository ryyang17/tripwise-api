package com.example.tripwise_api.service;

import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.dal.TripDAL;
import com.example.tripwise_api.exception.TripNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripService {
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