package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.Trip;
import com.example.tripwise_api.repository.TripRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class TripDAL {
    // TripRepository is injected here. DAL uses it for all database operations.
    private final TripRepository tripRepository;

    public TripDAL(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> getAllTrips() {
        // Direct database access via repository
        return tripRepository.findAll();
    }

    public Optional<Trip> getTripById(Integer id) {
        return tripRepository.findById(id);
    }

    public List<Trip> getTripsByUserId(Long userId) {
        return tripRepository.findByUserId(userId);
    }

    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    public void deleteTrip(Integer id) {
        tripRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return tripRepository.existsById(id);
    }
}
