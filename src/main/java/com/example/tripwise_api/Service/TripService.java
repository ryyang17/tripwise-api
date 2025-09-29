package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Trip;
import com.example.tripwise_api.Repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Transactional(readOnly = true)
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }


    @Transactional(readOnly = true)
    public Optional<Trip> getTripById(Integer id) {
        return tripRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<Trip> getTripsByUserId(Long userId) {
        return tripRepository.findByUserId(userId);
    }


    public Trip createTrip(Trip trip) {
        validateTrip(trip);
        return tripRepository.save(trip);
    }


    public Trip updateTrip(Integer id, Trip tripDetails) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        validateTrip(tripDetails);

        // Update fields
        existingTrip.setUserId(tripDetails.getUserId());
        existingTrip.setTripName(tripDetails.getTripName());
        existingTrip.setStartDate(tripDetails.getStartDate());
        existingTrip.setEndDate(tripDetails.getEndDate());
        existingTrip.setDestination(tripDetails.getDestination());
        existingTrip.setDescription(tripDetails.getDescription());
        existingTrip.setActive(tripDetails.isActive());

        return tripRepository.save(existingTrip);
    }


    public void deleteTrip(Integer id) {
        if (!tripRepository.existsById(id)) {
            throw new RuntimeException("Trip not found with id: " + id);
        }
        tripRepository.deleteById(id);
    }



    /**
     * Basic validation for trip data
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