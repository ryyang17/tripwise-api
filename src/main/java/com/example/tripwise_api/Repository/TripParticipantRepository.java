package com.example.tripwise_api.Repository;

import com.example.tripwise_api.Model.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Integer> {
    List<TripParticipant> findByTripId(Integer tripId);
    List<TripParticipant> findByUserId(Long userId);
}

