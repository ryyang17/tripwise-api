package com.example.tripwise_api.repository;

import com.example.tripwise_api.model.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Integer> {
    List<TripParticipant> findByTripId(Integer tripId);

    // Find participant by trip and user - useful for shared expense validation
    Optional<TripParticipant> findByTripIdAndUserId(Integer tripId, Long userId);

    // Check if a user is a participant in a trip
    boolean existsByTripIdAndUserId(Integer tripId, Long userId);

    // Bulk delete participants by trip id
    void deleteByTripId(Integer tripId);

    // Get all participants with their user details eagerly loaded
    @Query("SELECT tp FROM TripParticipant tp JOIN FETCH tp.user WHERE tp.tripId = :tripId")
    List<TripParticipant> findByTripIdWithUser(@Param("tripId") Integer tripId);
}
